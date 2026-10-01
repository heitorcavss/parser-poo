package minilang;

public enum TokenType {
    // palavras-chave de início e término do código
    INICIO("inicio"),
    FIM("fim"),

    // palavras reservadas
    INT("int"),
    DOUBLE("double"),
    IF("if"),
    ELSE("else"),
    WHILE("while"),

    // símbolos
    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),
    SEMICOLON(";"),
    ASSIGN("="),
    PLUS("+"),
    MINUS("-"),
    STAR("*"),
    SLASH("/"),
    EQ("=="),
    NEQ("!="),
    LT("<"),
    GT(">"),
    LE("<="),
    GE(">="),

    // tokens com valor
    IDENTIFIER("identificador"),
    NUMBER("número"),

    EOF("fim do arquivo");

    private final String description;

    TokenType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
