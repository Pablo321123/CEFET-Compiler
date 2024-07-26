import lexical.*;

public class LexerTest {

    public static void main(String[] args) {
        //try (Lexer l = new Lexer(args[0])) {
        try (Lexer l = new Lexer("ex1.txt")) {

            Token lex;
            do {
                lex = l.scan();
                System.out.printf("%02d: (\"%s\", %s)\n", l.line,
                        lex.getTag().name(), lex.getTag());
            } while (lex.getTag() != TokenType.EOF &&
                    lex.getTag() != TokenType.INVALID_TOKEN &&
                    lex.getTag() != TokenType.UNEXPECTED_EOF);

        } catch (Exception e) {
            System.err.println("Internal error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
