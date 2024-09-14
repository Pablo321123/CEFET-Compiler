package syntactic;

import java.io.IOException;
import java.util.Hashtable;

import lexical.Lexer;
import lexical.Symbol;
import lexical.Token;
import lexical.TokenType;
import semantic.SemanticAnaliser;
import semantic.SemanticAnaliser.VariableType;
import semantic.SemanticException;

/**
 * SyntacticAnalysis
 */
public class SyntacticAnalysis {

    private Lexer lex;
    private Token currentToken;
    private SemanticAnaliser sem;

    public SyntacticAnalysis(String filePath) throws IOException {
        lex = new Lexer(filePath);
        currentToken = lex.scan();
        sem = new SemanticAnaliser();
        // symbleTable = lex.getSymbleTable();
    }

    private void advance() throws IOException {
        currentToken = lex.scan();

        if (currentToken.getTag() == TokenType.COMMENT) {
            advance();
        }
    }

    private void eat(TokenType t) throws IOException, SyntaticException {
        if (currentToken.getTag() == t) {
            this.advance();
        } else {
            error();
        }
    }

    public void start() throws IOException, SyntaticException, SemanticException {
        this.program();
        eat(TokenType.EOF);
        System.out.println("Semantic Analysis Done!");
    }

    // program ::= app identifier body
    public void program() throws IOException, SyntaticException, SemanticException {
        if (currentToken.getTag() == TokenType.COMMENT) {
            advance();
        }

        switch (currentToken.getTag()) {
            case APP:
                advance();
                eat(TokenType.IDENTIFIER);
                this.body();
                eat(TokenType.EOF);
                break;

            default:
                error();
                break;
        }
    }

    // body ::= [ var decl-list] init stmt-list return
    private void body() throws IOException, SyntaticException, SemanticException {
        switch (currentToken.getTag()) {
            case VAR:
                advance();
                do {
                    declList();
                } while (currentToken.getTag() == TokenType.VAR);

                eat(TokenType.INIT);

                this.stmtList();

                eat(TokenType.RETURN);
                break;

            default:
                error();
                break;
        }
    }

    // decl-list ::= decl {";" decl}
    private void declList() throws IOException, SyntaticException, SemanticException {
        this.decl();

        if (currentToken.getTag() == TokenType.SEMICOLON) {
            eat(TokenType.SEMICOLON);
            declList();
        }
    }

    // decl ::= type ident-list
    private void decl() throws IOException, SyntaticException, SemanticException {
        VariableType type = this.type();
        this.identList(type);
    }

    // type ::= integer | real
    private VariableType type() throws IOException, SyntaticException {
        switch (currentToken.getTag()) {
            case INTEGER:
                eat(TokenType.INTEGER);
                return VariableType.INTEGER;
            case REAL:
                eat(TokenType.REAL);
                return VariableType.REAL;
            default:
                error();
                return VariableType.ERROR;
        }
    }

    // ident-list ::= identifier {"," identifier}
    private void identList(VariableType type) throws IOException, SyntaticException, SemanticException {
        switch (currentToken.getTag()) {
            case IDENTIFIER:
                sem.declare(type, currentToken.getLexeme(), lex.getLine());
                eat(TokenType.IDENTIFIER);
                if (currentToken.getTag() == TokenType.COMMA) {
                    eat(TokenType.COMMA);
                    identList(type);
                }
                break;
            default:
                error();
                break;
        }
    }

    // stmt-list ::= stmt {";" stmt}
    private void stmtList() throws IOException, SyntaticException, SemanticException {

        this.stmt();

        if (currentToken.getTag() == TokenType.SEMICOLON) {
            eat(TokenType.SEMICOLON);
            stmtList();
        }

    }

    // stmt ::= assign-stmt | if-stmt | repeat-stmt | read-stmt | write-stmt
    private void stmt() throws SyntaticException, IOException, SemanticException {
        switch (currentToken.getTag()) {
            case IDENTIFIER:
                this.assignStmt();
                break;
            case IF:
                this.ifStmt();
                break;
            case REPEAT:
                repeatStmt();
                break;
            case READ:
                readStmt();
                break;
            case WRITE:
                writeStmt();
                break;
            default:
                error();
                break;
        }
    }

    // assign-stmt ::= identifier ":=" simple_expr
    private void assignStmt() throws IOException, SyntaticException, SemanticException {
        Symbol var = sem.get_type(currentToken.getLexeme(), lex.getLine());
        eat(TokenType.IDENTIFIER);
        eat(TokenType.DOT_ASSIGN);
        VariableType exprType = this.simpleExpr();

        if (!sem.isAssignmentCompatible(var.getType(), exprType)) {
            sem.error("Atribuição invalida: Tipos incompativeis para " + var.getName() + "\nLinha: " + lex.getLine());
        }

    }

    // if-stmt ::= if condition then stmt-list end | if condition then stmt-list
    // else stmt-list end
    private void ifStmt() throws IOException, SyntaticException, SemanticException {
        eat(TokenType.IF);
        VariableType condType = this.condition();
        if (!sem.isTypeBoolean(condType)) {
            sem.error("Condição não booleana!\nLinha:" + lex.getLine());
        }
        eat(TokenType.THEN);
        stmtList();

        if (currentToken.getTag() == TokenType.END) {
            eat(TokenType.END);
        } else {
            eat(TokenType.ELSE);
            stmtList();
            eat(TokenType.END);
        }
    }

    // condition ::= expression
    private VariableType condition() throws IOException, SyntaticException, SemanticException {
        return this.expression();
    }

    // expression::=simple-expr|simple-expr relop simple-expr
    private VariableType expression() throws IOException, SyntaticException, SemanticException {
        VariableType left = this.simpleExpr();
        if (currentToken.getTag() == TokenType.ASSIGN
                || currentToken.getTag() == TokenType.GREATER
                || currentToken.getTag() == TokenType.GREATER_EQUAL
                || currentToken.getTag() == TokenType.LESS
                || currentToken.getTag() == TokenType.LESS_EQUAL
                || currentToken.getTag() == TokenType.NOT_EQUAL) {
            this.relop();
            VariableType right = this.simpleExpr();

            if (!sem.isOpTypeCompatible(left, right)) {
                sem.error("Tipos Incompatíveis na expressão!\nLinha:" + lex.getLine());
            }
            return VariableType.BOOLEAN;
        }
        return left;
    }

    // relop → "=" | ">" | ">=" | "<" | "<=" | "!="
    private void relop() throws IOException, SyntaticException {
        switch (currentToken.getTag()) {
            case ASSIGN:
                eat(TokenType.ASSIGN);
                break;
            case GREATER:
                eat(TokenType.GREATER);
                break;
            case GREATER_EQUAL:
                eat(TokenType.GREATER_EQUAL);
                break;
            case LESS:
                eat(TokenType.LESS);
                break;
            case LESS_EQUAL:
                eat(TokenType.LESS_EQUAL);
                break;
            case NOT_EQUAL:
                eat(TokenType.NOT_EQUAL);
                break;
            default:
                error();
                break;
        }
    }

    // simple-expr ::= term simple-expr'
    private VariableType simpleExpr() throws IOException, SyntaticException, SemanticException {
        VariableType termType = this.term();
        VariableType exprType = this.simpleExpr_(termType);
        return exprType;
    }

    // simple-expr’ ::= addop term simple-expr' | λ
    private VariableType simpleExpr_(VariableType type) throws IOException, SyntaticException, SemanticException {

        if (currentToken.getTag() == TokenType.PLUS ||
                currentToken.getTag() == TokenType.MINUS ||
                currentToken.getTag() == TokenType.OR) {
            TokenType op = currentToken.getTag();
            this.addop();
            VariableType termType = this.term();
            VariableType resultType = sem.getResultType(termType, type, op, 0);
            return this.simpleExpr_(resultType);
        }
        return type;

    }

    // addop → "+" | "-" | "||"
    private void addop() throws IOException, SyntaticException {
        switch (currentToken.getTag()) {
            case PLUS:
                eat(TokenType.PLUS);
                break;
            case MINUS:
                eat(TokenType.MINUS);
                break;
            case OR:
                eat(TokenType.OR);
                break;

            default:
                error();
                break;
        }
    }

    // term ::= factor-a term'
    private VariableType term() throws IOException, SyntaticException, SemanticException {
        VariableType factorType = factor_a();
        VariableType term_Type = term_(factorType);

        return term_Type;
    }

    // term' ::= mulop factor-a term' | λ
    private VariableType term_(VariableType type) throws IOException, SyntaticException, SemanticException {
        if (currentToken.getTag() == TokenType.MULTIPLY ||
                currentToken.getTag() == TokenType.DIVIDE ||
                currentToken.getTag() == TokenType.AND) {
            TokenType op = currentToken.getTag();
            mulop();
            VariableType factorType = factor_a();
            VariableType resultType = sem.getResultType(type, factorType, op, lex.getLine());
            return term_(resultType);
        }
        return type;
    }

    // mulop → "*" | "/" | "&&"
    private void mulop() throws IOException, SyntaticException {
        switch (currentToken.getTag()) {
            case MULTIPLY:
                eat(TokenType.MULTIPLY);
                break;
            case DIVIDE:
                eat(TokenType.DIVIDE);
                break;
            case AND:
                eat(TokenType.AND);
                break;
            default:
                error();
                break;
        }
    }

    // fator-a ::= factor | "!" factor | "-" factor
    private VariableType factor_a() throws IOException, SyntaticException, SemanticException {
        if (currentToken.getTag() == TokenType.NOT) {
            eat(TokenType.NOT);
            VariableType factorType = factor();
            if (!sem.isTypeBoolean(factorType)) {
                sem.error("Operador '!' apliacado a um termo não booleano!\nLinha:" + lex.getLine());
            }
            return VariableType.BOOLEAN;

        } else if (currentToken.getTag() == TokenType.MINUS) {
            eat(TokenType.MINUS);
            VariableType factorType = factor();
            if (!sem.isTypeNumeric(factorType)) {
                sem.error("Operação com operando não numerico!\nLinha:" + lex.getLine());
            }
            return factorType;
        } else {
            return factor();
        }
    }

    // factor ::= identifier | constant | "(" expression ")"
    private VariableType factor() throws IOException, SyntaticException, SemanticException {
        VariableType type;
        if (currentToken.getTag() == TokenType.IDENTIFIER) {
            Symbol var = sem.get_type(currentToken.getLexeme(), lex.getLine());
            eat(TokenType.IDENTIFIER);
            type = var.getType();
        } else if (currentToken.getTag() == TokenType.OPEN_PAR) {
            eat(TokenType.OPEN_PAR);
            type = expression();
            eat(TokenType.CLOSE_PAR);
        } else {
            type = this.constant();
        }

        return type;
    }

    // constant → integer_const | float_const
    private VariableType constant() throws IOException, SyntaticException {
        switch (currentToken.getTag()) {
            case INTEGER_CONST:
                eat(TokenType.INTEGER_CONST);
                return VariableType.INTEGER;
            case FLOAT_CONST:
                eat(TokenType.FLOAT_CONST);
                return VariableType.REAL;
            default:
                error();
                break;
        }
        return VariableType.ERROR;
    }

    // repeat-stmt ::= repeat stmt-list stmt-suffix
    private void repeatStmt() throws IOException, SyntaticException, SemanticException {
        switch (currentToken.getTag()) {
            case REPEAT:
                eat(TokenType.REPEAT);
                stmtList();
                stmtSuffix();
                break;
            default:
                error();
                break;
        }
    }

    // stmt-suffix ::= until condition
    private void stmtSuffix() throws IOException, SyntaticException, SemanticException {
        switch (currentToken.getTag()) {
            case UNTIL:
                eat(TokenType.UNTIL);
                VariableType condType = condition();

                if (!sem.isTypeBoolean(condType)) {
                    sem.error("Operador '!' apliacado a um termo não booleano!\nLinha:" + lex.getLine());
                }
                break;

            default:
                error();
                break;
        }
    }

    // read-stmt ::= read "(" identifier ")"
    private void readStmt() throws IOException, SyntaticException, SemanticException {
        switch (currentToken.getTag()) {
            case READ:
                eat(TokenType.READ);
                if (currentToken.getTag() == TokenType.OPEN_PAR) {
                    eat(TokenType.OPEN_PAR);
                    // Verificar se a variavel foi declarada
                    sem.get_type(currentToken.getLexeme(), lex.getLine());
                    eat(TokenType.IDENTIFIER);
                    eat(TokenType.CLOSE_PAR);
                } else {
                    error();
                }
                break;
            default:
                error();
                break;
        }
    }

    // write-stmt ::= write "(" writable ")"
    private void writeStmt() throws IOException, SyntaticException, SemanticException {
        switch (currentToken.getTag()) {
            case WRITE:
                eat(TokenType.WRITE);
                if (currentToken.getTag() == TokenType.OPEN_PAR) {
                    eat(TokenType.OPEN_PAR);
                    VariableType exprType = writable();
                    eat(TokenType.CLOSE_PAR);
                } else {
                    error();
                }
                break;

            default:
                error();
                break;
        }
    }

    // writable ::= simple-expr | literal
    private VariableType writable() throws IOException, SyntaticException, SemanticException {
        if (currentToken.getTag() == TokenType.LITERAL) {
            eat(TokenType.LITERAL);
            return VariableType.STRING;
        } else {
            return simpleExpr();
        }
    }

    private void error() throws SyntaticException {
        throw new SyntaticException(
                "\nToken " + this.currentToken.getTag().name() + " não esperado!\nLinha: " + lex.getLine() + "\n");
    }
}