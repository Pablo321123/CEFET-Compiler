package lexical;

public class Token {

    private TokenType tag;

    public Token(TokenType identifier) {
        tag = identifier;
    }

    @Override
    public String toString() {
        return "" + tag;
    }

    public TokenType getTag() {
        return tag;
    }

    public String getLexeme() {    
        return tag.getLexema();   
    }

}