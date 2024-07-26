package lexical;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class Lexer implements AutoCloseable {

    public int line = 1;
    private char ch = ' ';
    private FileReader file;
    private Hashtable<String, Word> words = new Hashtable<>();

    public Lexer(String fileName) throws FileNotFoundException {
        try {
            file = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Boa tarde! Arquivo não encontrado!");
            throw e;
        }
    }

    // Insere palavras reservadas na HashTable
    private void reserve(Word w) {
        words.put(w.getLexeme(), w);
    }

    /* Lê o próximo caractere do arquivo */
    private void readch() throws IOException {
        ch = (char) file.read();
    }

    /* Lê o próximo caractere do arquivo e verifica se é igual a c */
    private boolean readch(char c) throws IOException {
        readch();
        if (ch != c) {
            return false;
        } else {
            ch = ' ';
            return true;
        }
    }

    public Token scan() throws IOException {
        // Desconsidera delimitadores na entrada
        for (;; readch()) {
            if (ch == ' ' || Character.isISOControl(ch)) {
                continue;
            } else if (ch == '\n') {
                line++;
            } else {
                break;
            }
        }

        switch (ch) {
            // Operadores
            case '&':
                if (readch('&')) {
                    return Word.and;
                } else {
                    return new Token(TokenType.AND);// &&
                }
            case '|':
                if (readch('|')) {
                    return Word.or;
                } else {
                    return new Token(TokenType.OR); // ||
                }
            case '=':
                if (readch('=')) {
                    return Word.eq;
                } else {
                    return new Token(TokenType.ASSIGN);
                }
            case '<':
                if (readch('=')) {
                    return Word.le;
                } else {
                    return new Token(TokenType.LESS); // <
                }
            case '>':
                if (readch('=')) {
                    return Word.ge;
                } else {
                    return new Token(TokenType.GREATER); // >
                }
            default:
                break;
        }

        // Números
        if (Character.isDigit(ch)) {
            int value = 0;
            do {
                value = 10 * value + Character.digit(ch, 10);
                readch();
            } while (Character.isDigit(ch));

            return new Num(value);
        }

        // Identificadores
        if (Character.isLetter(ch)) {
            StringBuffer sb = new StringBuffer();
            do {
                sb.append(ch);
                readch();
            } while (Character.isLetterOrDigit(ch));

            String s = sb.toString();
            Word w = (Word) words.get(s);

            if (w != null) {
                return w;
            }

            w = new Word(s, TokenType.IDENTIFIER);
            words.put(s, w);
            return w;
        }

        // Caracteres não especificados
        Token t = new Token(TokenType.VAR);
        ch = ' ';
        return t;
    }


    public char getCh() {
        return ch;
    }

    public int getLine() {
        return line;
    }

    @Override
    public void close() throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'close'");
    }
}
