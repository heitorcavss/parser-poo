package minilang;

import java.util.ArrayList;

/**
 * Analisador sintático da MiniLang. Recebe a lista de tokens e tem um método para cada regra da gramática.
 *
 * <pre>
 * programa   -> inicio declaracao* ifElse fim
 * declaracao -> (int | double) ID ('=' expr)? ';'
 * ifElse     -> if '(' condicao ')' bloco else bloco
 * bloco      -> '{' atribuicao* '}'
 * atribuicao -> ID '=' expr ';'
 * condicao   -> expr (== | != | &lt; | &gt; | &lt;= | &gt;=) expr
 * expr       -> termo ((+ | -) termo)*
 * termo      -> fator ((* | /) fator)*
 * fator      -> NUMERO | ID | '(' expr ')' | '-' fator
 * </pre>
 *
 * Cada método devolve true se reconheceu a regra, ou false se encontrou erro
 * (o primeiro erro fica guardado e pode ser lido com {@link #getError()}).
 */
public class Parser {
    private final ArrayList<Token> tokens;
    private int current = 0;
    private SyntaxError error = null;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    /** @return true se o código é sintaticamente correto; senão consulte {@link #getError()}. */
    public boolean parse() {
        return programa();
    }

    public SyntaxError getError() {
        return error;
    }

    // ---------- regras da gramática ----------

    private boolean programa() {
        if (!expect(TokenType.INICIO, "o código deve começar com 'inicio'")) {
            return false;
        }
        while (check(TokenType.INT) || check(TokenType.DOUBLE)) {
            if (!declaracao()) {
                return false;
            }
        }
        if (!check(TokenType.IF)) {
            return fail("esperado o bloco 'if-else' (as declarações vêm antes dele), mas encontrado " + describe());
        }
        if (!ifElse()) {
            return false;
        }
        if (check(TokenType.IF)) {
            return fail("apenas um bloco if-else é permitido");
        }
        if (!expect(TokenType.FIM, "esperado 'fim' para encerrar o código")) {
            return false;
        }
        return expect(TokenType.EOF, "nenhum código é permitido após 'fim'");
    }

    private boolean declaracao() {
        advance(); // int | double
        if (!expect(TokenType.IDENTIFIER, "esperado nome da variável após o tipo")) {
            return false;
        }
        if (match(TokenType.ASSIGN) && !expr()) {
            return false;
        }
        return expect(TokenType.SEMICOLON, "esperado ';' ao final da declaração");
    }

    private boolean ifElse() {
        advance(); // if
        if (!expect(TokenType.LPAREN, "esperado '(' após 'if'")) {
            return false;
        }
        if (!condicao()) {
            return false;
        }
        if (!expect(TokenType.RPAREN, "esperado ')' ao fechar a condição do 'if'")) {
            return false;
        }
        if (!bloco()) {
            return false;
        }
        if (!expect(TokenType.ELSE, "esperado 'else' (o bloco if-else exige a cláusula else)")) {
            return false;
        }
        return bloco();
    }

    private boolean bloco() {
        if (!expect(TokenType.LBRACE, "esperado '{' para abrir o bloco")) {
            return false;
        }
        while (!check(TokenType.RBRACE)) {
            if (check(TokenType.EOF) || check(TokenType.FIM)) {
                return fail("bloco não fechado: esperado '}'");
            }
            if (!atribuicao()) {
                return false;
            }
        }
        advance(); // }
        return true;
    }

    private boolean atribuicao() {
        if (!expect(TokenType.IDENTIFIER, "esperado atribuição (variável '=' expressão ';')")) {
            return false;
        }
        if (!expect(TokenType.ASSIGN, "esperado '=' na atribuição")) {
            return false;
        }
        if (!expr()) {
            return false;
        }
        return expect(TokenType.SEMICOLON, "esperado ';' ao final da atribuição");
    }

    private boolean condicao() {
        if (!expr()) {
            return false;
        }
        if (!(check(TokenType.EQ) || check(TokenType.NEQ) || check(TokenType.LT)
                || check(TokenType.GT) || check(TokenType.LE) || check(TokenType.GE))) {
            return fail("esperado operador relacional (==, !=, <, >, <=, >=), mas encontrado " + describe());
        }
        advance();
        return expr();
    }

    private boolean expr() {
        if (!termo()) {
            return false;
        }
        while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
            advance();
            if (!termo()) {
                return false;
            }
        }
        return true;
    }

    private boolean termo() {
        if (!fator()) {
            return false;
        }
        while (check(TokenType.STAR) || check(TokenType.SLASH)) {
            advance();
            if (!fator()) {
                return false;
            }
        }
        return true;
    }

    private boolean fator() {
        if (match(TokenType.NUMBER) || match(TokenType.IDENTIFIER)) {
            return true;
        }
        if (match(TokenType.MINUS)) {
            return fator();
        }
        if (match(TokenType.LPAREN)) {
            if (!expr()) {
                return false;
            }
            return expect(TokenType.RPAREN, "esperado ')' para fechar a expressão");
        }
        return fail("esperado número, variável ou '(', mas encontrado " + describe());
    }

    // ---------- utilitários ----------

    private Token peek() {
        return tokens.get(current);
    }

    private boolean check(String type) {
        return peek().getType().equals(type);
    }

    private Token advance() {
        Token t = tokens.get(current);
        if (!t.getType().equals(TokenType.EOF)) {
            current++;
        }
        return t;
    }

    private boolean match(String type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }

    private boolean expect(String type, String message) {
        if (check(type)) {
            advance();
            return true;
        }
        return fail(message + ", mas encontrado " + describe());
    }

    /** Guarda o primeiro erro (na posição do token atual) e devolve false. */
    private boolean fail(String message) {
        if (error == null) {
            Token t = peek();
            error = new SyntaxError(message, t.getLine(), t.getColumn());
        }
        return false;
    }

    private String describe() {
        Token t = peek();
        return t.getType().equals(TokenType.EOF) ? "fim do arquivo" : "'" + t.getLexeme() + "'";
    }
}
