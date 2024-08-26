package syntactic;

import java.io.IOException;
import java.util.Hashtable;

import lexical.Lexer;
import lexical.Token;
import lexical.TokenType;

/**
 * SyntacticAnalysis
 */
public class SyntacticAnalysis {

    private Lexer lex;
    private Token currentToken;
    private Hashtable<String, TokenType> symbleTable;

    public SyntacticAnalysis(String filePath) throws IOException {
        lex = new Lexer(filePath);
        currentToken = lex.scan();
        symbleTable = lex.getSymbleTable();
    }

    private void advance() throws IOException {
        currentToken = lex.scan();
    }

    private void eat(TokenType t) throws IOException, SyntaticException {
        if (currentToken.getTag() == t) {
            this.advance();
        } else {
            error();
        }
    }

    public void start() throws IOException, SyntaticException {
        this.program();
        eat(TokenType.EOF);
        System.out.println("Semantic Analysis Done!");
    }

    // program ::= app identifier body
    public void program() throws IOException, SyntaticException {
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
    private void body() throws IOException, SyntaticException {
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
    private void declList() throws IOException, SyntaticException {
        this.decl();

        if (currentToken.getTag() == TokenType.SEMICOLON) {
            eat(TokenType.SEMICOLON);
            declList();
        }
    }

    // decl ::= type ident-list
    private void decl() throws IOException, SyntaticException {
        this.type();
        this.identList();
    }

    // type ::= integer | real
    private void type() throws IOException, SyntaticException {
        switch (currentToken.getTag()) {
            case INTEGER:
                eat(TokenType.INTEGER);
                break;
            case REAL:
                eat(TokenType.REAL);
                break;
            default:
                error();
                break;
        }
    }

    // ident-list ::= identifier {"," identifier}
    private void identList() throws IOException, SyntaticException {
        switch (currentToken.getTag()) {
            case IDENTIFIER:
                eat(TokenType.IDENTIFIER);
                if (currentToken.getTag() == TokenType.COMMA) {
                    eat(TokenType.COMMA);
                    identList();
                }
                break;
            default:
                error();
                break;
        }
    }

    // stmt-list ::= stmt {";" stmt}
    private void stmtList() throws IOException, SyntaticException {

        this.stmt();

        if (currentToken.getTag() == TokenType.SEMICOLON) {
            eat(TokenType.SEMICOLON);
            stmtList();
        }

    }

    // stmt ::= assign-stmt | if-stmt | repeat-stmt | read-stmt | write-stmt
    private void stmt() throws SyntaticException, IOException {
        switch (currentToken.getTag()) {
            case IDENTIFIER:
                this.assignStmt();
                break;
            case IF:
                this.ifStmt();
                break;
            case REPEAT:
                repeatStmt();
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
    private void assignStmt() throws IOException, SyntaticException {
        eat(TokenType.IDENTIFIER);
        eat(TokenType.DOT_ASSIGN);
        this.simpleExpr();
    }

    // if-stmt ::= if condition then stmt-list end | if condition then stmt-list
    // else stmt-list end
    private void ifStmt() throws IOException, SyntaticException {
        eat(TokenType.IF);
        this.condition();
        eat(TokenType.THEN);
        stmtList();

        if (currentToken.getTag() == TokenType.END) {
            return;
        } else {
            stmtList();
            eat(TokenType.END);
        }
    }

    // condition ::= expression
    private void condition() throws IOException, SyntaticException {
        this.expression();
    }

    // expression::=simple-expr|simple-expr relop simple-expr
    private void expression() throws IOException, SyntaticException {
        this.simpleExpr();
        if (currentToken.getTag() == TokenType.ASSIGN
                || currentToken.getTag() == TokenType.GREATER
                || currentToken.getTag() == TokenType.GREATER_EQUAL
                || currentToken.getTag() == TokenType.LESS
                || currentToken.getTag() == TokenType.LESS_EQUAL
                || currentToken.getTag() == TokenType.NOT_EQUAL) {
            this.relop();
            this.simpleExpr();
        }
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
    private void simpleExpr() throws IOException, SyntaticException {
        this.term();
        this.simpleExpr_();
    }

    // simple-expr’ ::= addop term simple-expr' | λ
    private void simpleExpr_() throws IOException, SyntaticException {

        if (currentToken.getTag() == TokenType.PLUS ||
                currentToken.getTag() == TokenType.MINUS ||
                currentToken.getTag() == TokenType.OR) {
            this.addop();
            this.term();
            this.simpleExpr_();
        }

    }

    // addop → "+" | "-" | "||"
    private void addop() throws IOException, SyntaticException {
        switch (currentToken.getTag()) {
            case PLUS:
                eat(TokenType.PLUS);
                break;
            case MINUS:
                eat(TokenType.PLUS);
                break;
            case OR:
                eat(TokenType.PLUS);
                break;

            default:
                error();
                break;
        }
    }

    // term ::= factor-a term'
    private void term() throws IOException, SyntaticException {
        factor_a();
        term_();
    }

    // term' ::= mulop factor-a term' | λ
    private void term_() throws IOException, SyntaticException {
        if (currentToken.getTag() == TokenType.MULTIPLY ||
                currentToken.getTag() == TokenType.DIVIDE ||
                currentToken.getTag() == TokenType.AND) {
            mulop();
            factor_a();
            term_();
        }
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
    private void factor_a() throws IOException, SyntaticException {
        if (currentToken.getTag() == TokenType.NOT) {
            eat(TokenType.NOT);
            factor();
        } else if (currentToken.getTag() == TokenType.MINUS) {
            eat(TokenType.MINUS);
            factor();
        } else {
            factor();
        }
    }

    // factor ::= identifier | constant | "(" expression ")"
    private void factor() throws IOException, SyntaticException {
        if (currentToken.getTag() == TokenType.IDENTIFIER) {
            eat(TokenType.IDENTIFIER);
        } else if (currentToken.getTag() == TokenType.OPEN_PAR) {
            eat(TokenType.OPEN_PAR);
            expression();
            eat(TokenType.CLOSE_PAR);
        } else {
            this.constant();
        }
    }

    // constant → integer_const | float_const
    private void constant() throws IOException, SyntaticException {
        switch (currentToken.getTag()) {
            case INTEGER_CONST:
                eat(TokenType.INTEGER_CONST);
                break;
            case FLOAT_CONST:
                eat(TokenType.FLOAT_CONST);
                break;
            default:
                error();
                break;
        }
    }

    // repeat-stmt ::= repeat stmt-list stmt-suffix
    private void repeatStmt() throws IOException, SyntaticException {
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
    private void stmtSuffix() throws IOException, SyntaticException {
        switch (currentToken.getTag()) {
            case UNTIL:
                condition();
                break;

            default:
                error();
                break;
        }
    }

    // read-stmt ::= read "(" identifier ")"
    private void readStmt() throws IOException, SyntaticException {
        switch (currentToken.getTag()) {
            case READ:
                eat(TokenType.READ);
                if (currentToken.getTag() == TokenType.OPEN_PAR) {
                    eat(TokenType.OPEN_PAR);
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
    private void writeStmt() throws IOException, SyntaticException {
        switch (currentToken.getTag()) {
            case WRITE:
                eat(TokenType.WRITE);
                if (currentToken.getTag() == TokenType.OPEN_PAR) {
                    eat(TokenType.OPEN_PAR);
                    writable();
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
    private void writable() throws IOException, SyntaticException {
        if (currentToken.getTag() == TokenType.LITERAL) {
            eat(TokenType.LITERAL);
        } else {
            simpleExpr();
        }
    }

    private void error() throws SyntaticException {
        throw new SyntaticException("\nToken " + this.currentToken + " não esperado!\nLinha: " + lex.getLine() + "\n");
    }
}