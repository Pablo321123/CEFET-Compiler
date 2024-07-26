package lexical;

public enum TokenType {

    // Palavras reservadas
    APP("app"), // app
    VAR("var"), // var
    INIT("init"), // init
    RETURN("return"), // return
    INTEGER("integer"), // integer
    REAL("real"), // real
    IF("if"), // if
    THEN("then"), // then
    END("end"), // end
    ELSE("else"), // else
    REPEAT("repeat"), // repeat
    UNTIL("until"), // until
    READ("read"), // read
    WRITE("write"), // write

    // Operadores e Pontuação
    OPEN_PAR("("), // (
    CLOSE_PAR(")"), // )
    NOT("!"), // !
    MINUS("-"), // -
    ASSIGN("="), // =
    GREATER(">"), // >
    GREATER_EQUAL(">="), // >=
    LESS("<"), // <
    LESS_EQUAL("<="), // <=
    NOT_EQUAL("!="), // !=
    PLUS("+"), // +
    OR("||"), // ||
    MULTIPLY("*"), // *
    DIVIDE("/"), // /
    AND("&&"), // &&
    DOT("."), // .
    SEMICOLON(";"), // ;
    DOT_ASSIGN(":="), // :=
    UNDERSCORE("_"), // _
    COMMA(","), // ,

    // Outros
    IDENTIFIER(""), // identifier
    INTEGER_CONST(""), // integer_const
    FLOAT_CONST(""), // float_const
    LITERAL(""), // literal
    EOF(""), // end of file
    INVALID_TOKEN(""), // invalid_token
    UNEXPECTED_EOF(""); // unexpected_eof

    private final String lexema;

    TokenType(String lexema) {
        this.lexema = lexema;
    }

    public String getLexema() {
        return lexema;
    }
}