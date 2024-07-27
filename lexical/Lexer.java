package lexical;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class Lexer implements AutoCloseable {

    public int line = 1;
    private char ch = ' ';
    private FileReader file;
    private Hashtable<String, Word> words = new Hashtable<>(); // Tabela de símbolos
    private ReservedBank rb;

    public Lexer(String fileName) throws FileNotFoundException {
        try {
            file = new FileReader(fileName);
            rb = new ReservedBank();
        } catch (FileNotFoundException e) {
            System.out.println("Boa tarde! Arquivo não encontrado!");
            throw e;
        }

        // Insere palavras reservadas na hashtable
        for (Word w : rb.reservedWords.values()) {
            reserve(new Word(w.getLexeme(), w.getTag()));
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
            if ((ch == ' ' || ch == '\t' || ch == '\r' || ch == '\b')) {
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
                    return new Token(TokenType.AND);
                    // return Word.and;
                } else {
                    return new Token(TokenType.INVALID_TOKEN);// &&
                }
            case '|':
                if (readch('|')) {
                    return new Token(TokenType.OR);
                    // return Word.or;
                } else {
                    return new Token(TokenType.INVALID_TOKEN); // ||
                }
            case '=':
                if (readch(' ')) {
                    return new Token(TokenType.ASSIGN);
                } else {
                    return new Token(TokenType.INVALID_TOKEN);
                }
            case '<':
                if (readch('=')) {
                    return new Token(TokenType.LESS_EQUAL);
                    // return Word.le;
                } else {
                    return new Token(TokenType.LESS); // <
                }
            case '>':
                if (readch('=')) {
                    return new Token(TokenType.GREATER_EQUAL);
                    // return Word.ge;
                } else {
                    return new Token(TokenType.GREATER); // >
                }
            case '(': // OPEN_PAR
                readch();
                return new Token(TokenType.OPEN_PAR);
            case ')': // CLOSE_PAR
                readch();
                return new Token(TokenType.CLOSE_PAR);
            case '!': // NOT
                if (readch('=')) { // NOT_EQUAL
                    return new Token(TokenType.NOT_EQUAL);
                } else {
                    return new Token(TokenType.NOT);
                }
            case '-': // MINUS
                readch();
                return new Token(TokenType.MINUS);
            case '+': // PLUS
                readch();
                return new Token(TokenType.PLUS);

            case '*': // MULTIPLY
                if (readch(' ')) {
                    return new Token(TokenType.MULTIPLY);
                } else {
                    return new Token(TokenType.INVALID_TOKEN);
                }
            case '/': // DIVIDE
                if (readch(' ')) {
                    return new Token(TokenType.DIVIDE);
                } else {
                    return new Token(TokenType.INVALID_TOKEN);
                }
            case ';': // SEMICOLON
                readch();
                return new Token(TokenType.SEMICOLON);
            case ':': // DOT_ASSIGN
                if (readch('=')) {
                    return new Token(TokenType.DOT_ASSIGN);
                } else {
                    return new Token(TokenType.INVALID_TOKEN);
                }
            case '_': // UNDERSCORE
                readch();
                return new Token(TokenType.UNDERSCORE);
            case ',': // COMMA
                if (readch(' ')) {
                    return new Token(TokenType.COMMA);
                } else {
                    return new Token(TokenType.INVALID_TOKEN);
                }
            default:
                break;
        }

        // Números
        if (Character.isDigit(ch)) {

            int numberState = 1;
            StringBuilder number = new StringBuilder();
            boolean real = false;
            int value = 0;

            do {
                switch (numberState) {
                    case 1:
                        value = 10 * value + Character.digit(ch, 10);
                        if (value > 0 && value <= 9) {
                            numberState = 2;
                        } else {
                            numberState = 3;
                        }
                        number.append(ch);
                        readch();
                        continue;
                    case 2:
                        if (readch('.')) {
                            numberState = 4;
                            number.append('.');
                            readch();
                        } else {
                            numberState = 2;
                            number.append(ch);
                        }
                        continue;
                    case 3:
                        if (readch('.')) {
                            number.append(ch);
                            numberState = 4;
                        } else {
                            number.append(ch);
                            numberState = -1;
                            return new Token(TokenType.INVALID_TOKEN);
                        }
                        continue;
                    case 4:
                        real = true;
                        number.append(ch);
                        readch();
                        // Sintatico?
                        // if (!Character.isDigit(ch) && (ch != ' ' && ch != '\n' && ch != '\t' && ch != ';')) {
                        //     numberState = -1;
                        // }
                        continue;

                    default:
                        System.out.println("Boa tarde!\nLinha " + line + ": Má formação de número real!" + "\nNúmero:"
                                + number + "\nDeu pal");
                        return new Token(TokenType.INVALID_TOKEN);
                }

            } while (Character.isDigit(ch));

            if (real) {
                return new Num(Float.parseFloat(number.toString()));
            } else {
                return new Num(Integer.parseInt(number.toString()));
            }
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
        Token t = new Word("" + ch, TokenType.INVALID_TOKEN);
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
