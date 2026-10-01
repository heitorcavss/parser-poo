package minilang;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Transforma o código-fonte em uma lista de tokens (sempre terminada por EOF). */
public class Lexer {
    private static final Map<String, TokenType> KEYWORDS = Map.of(
            "inicio", TokenType.INICIO,
            "fim", TokenType.FIM,
            "int", TokenType.INT,
            "double", TokenType.DOUBLE,
            "if", TokenType.IF,
            "else", TokenType.ELSE,
            "while", TokenType.WHILE);

    private final String src;
    private int pos = 0;
    private int line = 1;
    private int col = 1;

    public Lexer(String src) {
        this.src = src;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (true) {
            skipWhitespace();
            if (pos >= src.length()) {
                tokens.add(new Token(TokenType.EOF, "", line, col));
                return tokens;
            }
            tokens.add(next());
        }
    }

    private Token next() {
        int startLine = line, startCol = col;
        char c = peek();

        if (Character.isLetter(c) || c == '_') {
            StringBuilder sb = new StringBuilder();
            while (pos < src.length() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
                sb.append(advance());
            }
            String word = sb.toString();
            return new Token(KEYWORDS.getOrDefault(word, TokenType.IDENTIFIER), word, startLine, startCol);
        }

        if (Character.isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            while (pos < src.length() && Character.isDigit(peek())) sb.append(advance());
            if (pos < src.length() && peek() == '.') {
                sb.append(advance());
                if (pos >= src.length() || !Character.isDigit(peek())) {
                    throw new SyntaxError("número inválido '" + sb + "': esperado dígito após '.'", startLine, startCol);
                }
                while (pos < src.length() && Character.isDigit(peek())) sb.append(advance());
            }
            return new Token(TokenType.NUMBER, sb.toString(), startLine, startCol);
        }

        advance();
        switch (c) {
            case '(': return tok(TokenType.LPAREN, "(", startLine, startCol);
            case ')': return tok(TokenType.RPAREN, ")", startLine, startCol);
            case '{': return tok(TokenType.LBRACE, "{", startLine, startCol);
            case '}': return tok(TokenType.RBRACE, "}", startLine, startCol);
            case ';': return tok(TokenType.SEMICOLON, ";", startLine, startCol);
            case '+': return tok(TokenType.PLUS, "+", startLine, startCol);
            case '-': return tok(TokenType.MINUS, "-", startLine, startCol);
            case '*': return tok(TokenType.STAR, "*", startLine, startCol);
            case '/': return tok(TokenType.SLASH, "/", startLine, startCol);
            case '=': return match('=')
                    ? tok(TokenType.EQ, "==", startLine, startCol)
                    : tok(TokenType.ASSIGN, "=", startLine, startCol);
            case '<': return match('=')
                    ? tok(TokenType.LE, "<=", startLine, startCol)
                    : tok(TokenType.LT, "<", startLine, startCol);
            case '>': return match('=')
                    ? tok(TokenType.GE, ">=", startLine, startCol)
                    : tok(TokenType.GT, ">", startLine, startCol);
            case '!':
                if (match('=')) return tok(TokenType.NEQ, "!=", startLine, startCol);
                throw new SyntaxError("caractere '!' isolado; você quis dizer '!='?", startLine, startCol);
            default:
                throw new SyntaxError("caractere inesperado '" + c + "'", startLine, startCol);
        }
    }

    private Token tok(TokenType type, String lexeme, int l, int c) {
        return new Token(type, lexeme, l, c);
    }

    private boolean match(char expected) {
        if (pos < src.length() && peek() == expected) {
            advance();
            return true;
        }
        return false;
    }

    private void skipWhitespace() {
        while (pos < src.length() && Character.isWhitespace(peek())) advance();
    }

    private char peek() {
        return src.charAt(pos);
    }

    private char advance() {
        char c = src.charAt(pos++);
        if (c == '\n') {
            line++;
            col = 1;
        } else {
            col++;
        }
        return c;
    }
}
