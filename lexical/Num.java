package lexical;

public class Num extends Token {
    private int valueInteger;
    private float valueReal;
    private boolean real = false;

    Num(int valueInteger) {
        super(TokenType.INTEGER_CONST);
        this.valueInteger = valueInteger;
    }

    Num(float valueReal) {
        super(TokenType.FLOAT_CONST);
        this.valueReal = valueReal;
        real = true;
    }

    @Override
    public String toString() {
        return "" + valueInteger;
    }

    public String getLexeme() {
        if (real) {
            return Float.toString(valueReal);
        } else {
            return Integer.toString(valueInteger);
        }
    }

}
