package minilang;

import java.util.ArrayList;

/** Transforma o código-fonte em uma lista de tokens (sempre terminada por EOF, se não houver erro). */
public class Lexer {
    private final String src;
    private int pos = 0;
    private int line = 1;
    private int col = 1;
    private SyntaxError error = null;

    public Lexer(String src) {
        this.src = src;
    }

    public boolean hasError() {
        return error != null;
    }

    public SyntaxError getError() {
        return error;
    }

    /** Em caso de erro léxico, para e devolve os tokens lidos até ali; consulte {@link #getError()}. */
    public ArrayList<Token> tokenize() {
        ArrayList<Token> tokens = new ArrayList<>();
        while (true) {
            skipWhitespace();
            if (pos >= src.length()) {
                tokens.add(new Token(TokenType.EOF, "", line, col));
                return tokens;
            }
            Token t = next();
            if (t == null) {
                return tokens;
            }
            tokens.add(t);
        }
    }

    private Token next() {
        int startLine = line;
        int startCol = col;
        char c = peek();

        if (Character.isLetter(c) || c == '_') {
            StringBuilder sb = new StringBuilder();
            while (pos < src.length() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
                sb.append(advance());
            }
            String word = sb.toString();
            return new Token(isKeyword(word) ? word : TokenType.IDENTIFIER, word, startLine, startCol);
        }

        if (Character.isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            while (pos < src.length() && Character.isDigit(peek())) {
                sb.append(advance());
            }
            if (pos < src.length() && peek() == '.') {
                sb.append(advance());
                if (pos >= src.length() || !Character.isDigit(peek())) {
                    return fail("número inválido '" + sb + "': esperado dígito após '.'", startLine, startCol);
                }
                while (pos < src.length() && Character.isDigit(peek())) {
                    sb.append(advance());
                }
            }
            return new Token(TokenType.NUMBER, sb.toString(), startLine, startCol);
        }

        advance();
        switch (c) {
            case '(': return new Token(TokenType.LPAREN, "(", startLine, startCol);
            case ')': return new Token(TokenType.RPAREN, ")", startLine, startCol);
            case '{': return new Token(TokenType.LBRACE, "{", startLine, startCol);
            case '}': return new Token(TokenType.RBRACE, "}", startLine, startCol);
            case ';': return new Token(TokenType.SEMICOLON, ";", startLine, startCol);
            case '+': return new Token(TokenType.PLUS, "+", startLine, startCol);
            case '-': return new Token(TokenType.MINUS, "-", startLine, startCol);
            case '*': return new Token(TokenType.STAR, "*", startLine, startCol);
            case '/': return new Token(TokenType.SLASH, "/", startLine, startCol);
            case '=':
                return match('=')
                        ? new Token(TokenType.EQ, "==", startLine, startCol)
                        : new Token(TokenType.ASSIGN, "=", startLine, startCol);
            case '<':
                return match('=')
                        ? new Token(TokenType.LE, "<=", startLine, startCol)
                        : new Token(TokenType.LT, "<", startLine, startCol);
            case '>':
                return match('=')
                        ? new Token(TokenType.GE, ">=", startLine, startCol)
                        : new Token(TokenType.GT, ">", startLine, startCol);
            case '!':
                if (match('=')) {
                    return new Token(TokenType.NEQ, "!=", startLine, startCol);
                }
                return fail("caractere '!' isolado; você quis dizer '!='?", startLine, startCol);
            default:
                return fail("caractere inesperado '" + c + "'", startLine, startCol);
        }
    }

    private Token fail(String message, int l, int c) {
        error = new SyntaxError(message, l, c);
        return null;
    }

    private boolean isKeyword(String word) {
        for (String k : TokenType.KEYWORDS) {
            if (k.equals(word)) {
                return true;
            }
        }
        return false;
    }

    private boolean match(char expected) {
        if (pos < src.length() && peek() == expected) {
            advance();
            return true;
        }
        return false;
    }

    private void skipWhitespace() {
        while (pos < src.length() && Character.isWhitespace(peek())) {
            advance();
        }
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
