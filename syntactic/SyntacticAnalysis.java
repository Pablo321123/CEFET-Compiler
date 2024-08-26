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

    private void eat(TokenType t) throws IOException {
        if (currentToken.getTag() == t) {
            this.advance();
        } else {
            System.err.println("Token " + t + " não esperado");
        }
    }

    public void start() throws IOException {
        this.program();
        eat(TokenType.EOF);
        System.out.println("Semantic Analysis Done!");
    }

    // program ::= app identifier body
    public void program() throws IOException {
        switch (currentToken.getTag()) {
            case APP:
                advance();
                eat(TokenType.IDENTIFIER);
                this.body();
                break;

            default:
                break;
        }
    }

    // body ::= [ var decl-list] init stmt-list return
    private void body() throws IOException {
        switch (currentToken.getTag()) {
            case VAR:
                advance();
                do {
                    declList();
                } while (currentToken.getTag() == TokenType.VAR);

                eat(TokenType.INIT);

                this.stmtList();

                break;

            default:
                break;
        }
    }

    // decl-list ::= decl {";" decl}
    private void declList() throws IOException {
        this.decl();

        if (currentToken.getTag() == TokenType.SEMICOLON) {
            eat(TokenType.SEMICOLON);
            declList();
        }
    }

    // decl ::= type ident-list
    private void decl() throws IOException {
        this.type();
        this.identList();
    }

    // type ::= integer | real
    private void type() throws IOException {
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
    private void identList() throws IOException {
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
    private void stmtList() throws IOException {
        System.out.println(currentToken.getTag());
    }

    private void error() {
        System.err.println("Token " + this.currentToken + " não esperado!");
        return;
    }
}