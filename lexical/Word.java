package lexical;

import java.util.HashMap;
import java.util.Map;

public class Word extends Token {

    private String lexeme;   

    public static final Word and = new Word("&&", TokenType.AND);
    public static final Word or = new Word("||", TokenType.OR);
    public static final Word eq = new Word(":=", TokenType.DOT_ASSIGN);
    public static final Word ne = new Word("!=", TokenType.NOT_EQUAL);
    public static final Word le = new Word("<=", TokenType.LESS_EQUAL);
    public static final Word ge = new Word(">=", TokenType.GREATER_EQUAL);
    // public static final Word True = new Word("true", TokenType.TRUE);
    // public static final Word False = new Word("false", TokenType.FALSE);

    public Word(String s, TokenType identifier) {
        super(identifier);
        lexeme = s;
    }
    

    @Override
    public String toString() {
        return "" + lexeme;
    }

    public String getLexeme() {
        return lexeme;
    }

}
