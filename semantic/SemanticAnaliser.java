package semantic;

import java.util.Hashtable;

import lexical.Symbol;
import lexical.Token;
import lexical.TokenType;
import syntactic.SyntaticException;

public class SemanticAnaliser {

    public enum VariableType {
        INTEGER,
        REAL,
        BOOLEAN,
        STRING,
        ERROR
    }

    private Hashtable<String, Symbol> symbolTable;

    public SemanticAnaliser() {
        symbolTable = new Hashtable<String, Symbol>();
    }

    // Add variable in Symbol Table
    public void declare(VariableType type, String name, int line) throws SemanticException {
        if (symbolTable.containsKey(name)) {
            error("A variavel " + name + "ja foi declarada na linha " + line);
        } else {
            symbolTable.put(name, new Symbol(type, name, line));
        }
    }

    // Verify varible type
    public Symbol get_type(String variableName) throws SemanticException {
        if (symbolTable.get(variableName) == null) {
            error("A variavel " + variableName + " não foi declarada!");
        }
        return symbolTable.get(variableName);
    }

    public boolean isAssignmentCompatible(VariableType varType, VariableType exprType) {
        if (varType == VariableType.REAL && (exprType == VariableType.INTEGER || exprType == VariableType.REAL)) {
            return true;
        }
        return varType == exprType;
    }

    public boolean isOpTypeCompatible(VariableType left, VariableType right) {
        return left == right || (left == VariableType.REAL && right == VariableType.INTEGER)
                || (left == VariableType.INTEGER && right == VariableType.REAL);
    }

    public Hashtable<String, Symbol> getSymbolTable() {
        return symbolTable;
    }

    public VariableType getResultType(VariableType left, VariableType right, TokenType op, int line)
            throws SemanticException {
        if (op == TokenType.PLUS || op == TokenType.MINUS || op == TokenType.MULTIPLY || op == TokenType.DIVIDE) {
            if ((left == VariableType.INTEGER || left == VariableType.REAL)
                    && (right == VariableType.INTEGER || right == VariableType.REAL)) {
                if (left == VariableType.REAL || right == VariableType.REAL) {
                    return VariableType.REAL;
                } else {
                    return VariableType.INTEGER;
                }
            } else {
                throw new SemanticException(
                        "Erro na linha " + line + ": Operação aritmética com operandos não numéricos.");
            }
        } else if (op == TokenType.OR || op == TokenType.AND) {
            if (left == VariableType.BOOLEAN && right == VariableType.BOOLEAN) {
                return VariableType.BOOLEAN;
            } else {
                throw new SemanticException("Erro na linha " + line + ": Operação lógica com operandos não booleanos.");
            }
        } else {
            throw new SemanticException("Erro na linha " + line + ": Operador desconhecido.");
        }
    }

    public boolean isTypeBollean(VariableType type) {
        return type == VariableType.BOOLEAN;
    }

    public boolean isTypeNumeric(VariableType type) {
        return type == VariableType.INTEGER || type != VariableType.REAL;
    }

    public void error(String msg) throws SemanticException {
        throw new SemanticException(msg);
    }

}
