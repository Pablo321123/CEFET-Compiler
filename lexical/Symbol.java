package lexical;

import semantic.SemanticAnaliser.VariableType;

public class Symbol {
    private VariableType type;
    private String name;
    private int line;

    public Symbol(VariableType type, String name, int line) {
        this.type = type;
        this.name = name;
        this.line = line;
    }

    public VariableType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getLine() {
        return line;
    }

}
