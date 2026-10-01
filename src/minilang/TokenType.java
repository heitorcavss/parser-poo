package minilang;

/** Tipos de token. Cada tipo é uma constante de texto (o próprio símbolo ou palavra, quando existir). */
public class TokenType {
    public static final String INICIO = "inicio";
    public static final String FIM = "fim";

    public static final String INT = "int";
    public static final String DOUBLE = "double";
    public static final String IF = "if";
    public static final String ELSE = "else";
    public static final String WHILE = "while";

    public static final String LPAREN = "(";
    public static final String RPAREN = ")";
    public static final String LBRACE = "{";
    public static final String RBRACE = "}";
    public static final String SEMICOLON = ";";
    public static final String ASSIGN = "=";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String STAR = "*";
    public static final String SLASH = "/";
    public static final String EQ = "==";
    public static final String NEQ = "!=";
    public static final String LT = "<";
    public static final String GT = ">";
    public static final String LE = "<=";
    public static final String GE = ">=";

    public static final String IDENTIFIER = "identificador";
    public static final String NUMBER = "número";
    public static final String EOF = "fim do arquivo";

    /** Palavras que o Lexer deve reconhecer como palavras reservadas (o tipo é a própria palavra). */
    public static final String[] KEYWORDS = {INICIO, FIM, INT, DOUBLE, IF, ELSE, WHILE};

    private TokenType() {
        // classe de constantes: não deve ser instanciada
    }
}
