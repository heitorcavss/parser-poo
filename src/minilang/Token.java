package minilang;

public record Token(TokenType type, String lexeme, int line, int column) {

    @Override
    public String toString() {
        return type + "('" + lexeme + "') @" + line + ":" + column;
    }
}
