package lexical;

public class Num extends Token{
    private int value;

    Num(int value) {
        super(TokenType.INTEGER_CONST);
        value = this.value;
    }

    @Override
    public String toString() {
        return "" + value;
    }

}
