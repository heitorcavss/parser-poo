package minilang;

import java.util.ArrayList;

/**
 * Encapsula a lista de tokens, a posição atual e o primeiro erro encontrado.
 * As regras da gramática só enxergam este objeto, nunca a lista diretamente.
 */
public class TokenStream {
    private final ArrayList<Token> tokens;
    private int current = 0;
    private SyntaxError error = null;

    public TokenStream(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public Token peek() {
        return tokens.get(current);
    }

    public boolean check(String type) {
        return peek().getType().equals(type);
    }

    public boolean checkAny(String[] types) {
        for (String type : types) {
            if (check(type)) {
                return true;
            }
        }
        return false;
    }

    public Token advance() {
        Token t = tokens.get(current);
        if (!t.getType().equals(TokenType.EOF)) {
            current++;
        }
        return t;
    }

    public boolean match(String type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }

    /** Registra o erro na posição do token atual. Só o primeiro erro é guardado. */
    public void reportError(String message) {
        if (error == null) {
            Token t = peek();
            error = new SyntaxError(message, t.getLine(), t.getColumn());
        }
    }

    public boolean hasError() {
        return error != null;
    }

    public SyntaxError getError() {
        return error;
    }

    /** Texto do token atual para mensagens de erro. */
    public String describeCurrent() {
        Token t = peek();
        return t.getType().equals(TokenType.EOF) ? "fim do arquivo" : "'" + t.getLexeme() + "'";
    }
}
