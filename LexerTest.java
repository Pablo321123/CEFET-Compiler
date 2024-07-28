import lexical.*;

public class LexerTest {

    public static void main(String[] args) {

        // try (Lexer l = new Lexer(args[0])) {
        try (Lexer l = new Lexer("ex8.txt")) {
            Token lex;
            do {
                lex = l.scan();
                if (lex.getTag() == TokenType.COMMENT) {
                    continue;
                } else {
                    l.addSymbleTable(lex.getLexeme(), lex.getTag());
                    System.out.printf("%02d: (\"%s\", %s)\n", l.line, lex.getLexeme(), lex.getTag());
                }
            } while (lex.getTag() != TokenType.EOF &&
                    lex.getTag() != TokenType.INVALID_TOKEN &&
                    lex.getTag() != TokenType.UNEXPECTED_EOF);

            System.out.println("+-----------Symble Table----------------+");

            l.getSymbleTable();

        } catch (Exception e) {
            System.err.println("Internal error: " + e.getMessage());
            // e.printStackTrace();
        }
    }

}
