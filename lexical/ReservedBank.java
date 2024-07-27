package lexical;

import java.util.HashMap;

public class ReservedBank {

    protected HashMap<String, Word> reservedWords;

    public ReservedBank() {
        reservedWords = new HashMap<String, Word>();
        createReservedWords();
    }

    private void createReservedWords() {
        reservedWords.put("app", new Word("app", TokenType.APP));
        reservedWords.put("var", new Word("var", TokenType.VAR));
        reservedWords.put("init", new Word("init", TokenType.INIT));
        reservedWords.put("return", new Word("return", TokenType.RETURN));
        reservedWords.put("integer", new Word("integer", TokenType.INTEGER));
        reservedWords.put("real", new Word("real", TokenType.REAL));
        reservedWords.put("if", new Word("if", TokenType.IF));
        reservedWords.put("then", new Word("then", TokenType.THEN));
        reservedWords.put("end", new Word("end", TokenType.END));
        reservedWords.put("else", new Word("else", TokenType.ELSE));
        reservedWords.put("repeat", new Word("repeat", TokenType.REPEAT));
        reservedWords.put("until", new Word("until", TokenType.UNTIL));
        reservedWords.put("read", new Word("read", TokenType.READ));
        reservedWords.put("write", new Word("write", TokenType.WRITE));
        reservedWords.put("int", new Word("int", TokenType.INTEGER));
        reservedWords.put("real", new Word("real", TokenType.REAL));
        reservedWords.put("(", new Word("(", TokenType.OPEN_PAR));
        reservedWords.put(")", new Word(")", TokenType.CLOSE_PAR));
        reservedWords.put("!", new Word("!", TokenType.NOT));
        reservedWords.put("!=", new Word("!=", TokenType.NOT_EQUAL));
        reservedWords.put("-", new Word("-", TokenType.MINUS));
        reservedWords.put("=", new Word("=", TokenType.ASSIGN));
        reservedWords.put(">", new Word(">", TokenType.GREATER));
        reservedWords.put(">=", new Word(">=", TokenType.GREATER_EQUAL));
        reservedWords.put("<", new Word("<", TokenType.LESS));
        reservedWords.put("<=", new Word("<=", TokenType.LESS_EQUAL));
        reservedWords.put("+", new Word("+", TokenType.PLUS));
        reservedWords.put("||", new Word("||", TokenType.OR));
        reservedWords.put("*", new Word("*", TokenType.MULTIPLY));
        reservedWords.put("/", new Word("/", TokenType.DIVIDE));
        reservedWords.put("&&", new Word("&&", TokenType.AND));
        reservedWords.put(".", new Word(".", TokenType.DOT));
        reservedWords.put(";", new Word(";", TokenType.SEMICOLON));
        reservedWords.put(":=", new Word(":=", TokenType.DOT_ASSIGN));
        reservedWords.put("_", new Word("_", TokenType.UNDERSCORE));
        reservedWords.put(",", new Word(",", TokenType.COMMA));
    }
}
