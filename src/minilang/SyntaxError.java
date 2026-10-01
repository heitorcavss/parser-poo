package minilang;

public class SyntaxError extends RuntimeException {
    private final int line;
    private final int column;

    public SyntaxError(String message, int line, int column) {
        super("Erro sintático [linha " + line + ", coluna " + column + "]: " + message);
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
