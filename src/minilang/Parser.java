package minilang;

import java.util.List;

/**
 * Analisador sintático descendente recursivo (LL(1)) da MiniLang.
 * Cada método corresponde a uma regra da gramática abaixo.
 *
 * <pre>
 * programa     -> INICIO declaracao* ifElse FIM EOF
 * declaracao   -> ( int | double ) ID ( '=' expr )? ';'
 * ifElse       -> if '(' condicao ')' bloco else bloco
 * bloco        -> '{' comando* '}'
 * comando      -> atribuicao | while
 * atribuicao   -> ID '=' expr ';'
 * while        -> while '(' condicao ')' bloco
 * condicao     -> expr ( '==' | '!=' | '<' | '>' | '<=' | '>=' ) expr
 * expr         -> termo ( ( '+' | '-' ) termo )*
 * termo        -> fator ( ( '*' | '/' ) fator )*
 * fator        -> NUMERO | ID | '(' expr ')' | '-' fator
 * </pre>
 */
public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /** Valida o programa inteiro; lança {@link SyntaxError} no primeiro erro encontrado. */
    public void parse() {
        programa();
    }

    // ---- regras da gramática ----

    private void programa() {
        consume(TokenType.INICIO, "o código deve começar com 'inicio'");
        while (check(TokenType.INT) || check(TokenType.DOUBLE)) {
            declaracao();
        }
        if (!check(TokenType.IF)) {
            throw error("esperado o bloco 'if' (declarações devem vir antes e o 'if-else' é obrigatório), mas encontrado " + describe(peek()));
        }
        ifElse();
        if (check(TokenType.IF)) {
            throw error("apenas um bloco if-else é permitido");
        }
        consume(TokenType.FIM, "esperado 'fim' para encerrar o código");
        consume(TokenType.EOF, "nenhum código é permitido após 'fim'");
    }

    private void declaracao() {
        advance(); // int | double
        consume(TokenType.IDENTIFIER, "esperado nome da variável após o tipo");
        if (match(TokenType.ASSIGN)) {
            expr();
        }
        consume(TokenType.SEMICOLON, "esperado ';' ao final da declaração");
    }

    private void ifElse() {
        consume(TokenType.IF, "esperado 'if'");
        consume(TokenType.LPAREN, "esperado '(' após 'if'");
        condicao();
        consume(TokenType.RPAREN, "esperado ')' ao fechar a condição do 'if'");
        bloco();
        consume(TokenType.ELSE, "esperado 'else' (o bloco if-else exige a cláusula else)");
        bloco();
    }

    private void bloco() {
        consume(TokenType.LBRACE, "esperado '{' para abrir o bloco");
        while (!check(TokenType.RBRACE)) {
            if (check(TokenType.EOF) || check(TokenType.FIM)) {
                throw error("bloco não fechado: esperado '}'");
            }
            comando();
        }
        advance(); // }
    }

    private void comando() {
        if (check(TokenType.IDENTIFIER)) {
            atribuicao();
        } else if (check(TokenType.WHILE)) {
            whileCmd();
        } else {
            throw error("esperado comando (atribuição ou 'while'), mas encontrado " + describe(peek()));
        }
    }

    private void atribuicao() {
        advance(); // ID
        consume(TokenType.ASSIGN, "esperado '=' na atribuição");
        expr();
        consume(TokenType.SEMICOLON, "esperado ';' ao final da atribuição");
    }

    private void whileCmd() {
        advance(); // while
        consume(TokenType.LPAREN, "esperado '(' após 'while'");
        condicao();
        consume(TokenType.RPAREN, "esperado ')' ao fechar a condição do 'while'");
        bloco();
    }

    private void condicao() {
        expr();
        if (check(TokenType.EQ) || check(TokenType.NEQ) || check(TokenType.LT)
                || check(TokenType.GT) || check(TokenType.LE) || check(TokenType.GE)) {
            advance();
        } else {
            throw error("esperado operador relacional (==, !=, <, >, <=, >=), mas encontrado " + describe(peek()));
        }
        expr();
    }

    private void expr() {
        termo();
        while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
            advance();
            termo();
        }
    }

    private void termo() {
        fator();
        while (check(TokenType.STAR) || check(TokenType.SLASH)) {
            advance();
            fator();
        }
    }

    private void fator() {
        if (match(TokenType.NUMBER) || match(TokenType.IDENTIFIER)) return;
        if (match(TokenType.MINUS)) {
            fator();
            return;
        }
        if (match(TokenType.LPAREN)) {
            expr();
            consume(TokenType.RPAREN, "esperado ')' para fechar a expressão");
            return;
        }
        throw error("esperado número, variável ou '(', mas encontrado " + describe(peek()));
    }

    // ---- utilitários ----

    private Token peek() {
        return tokens.get(current);
    }

    private boolean check(TokenType type) {
        return peek().type() == type;
    }

    private Token advance() {
        Token t = tokens.get(current);
        if (t.type() != TokenType.EOF) current++;
        return t;
    }

    private boolean match(TokenType type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(message + ", mas encontrado " + describe(peek()));
    }

    private SyntaxError error(String message) {
        Token t = peek();
        return new SyntaxError(message, t.line(), t.column());
    }

    private static String describe(Token t) {
        return t.type() == TokenType.EOF ? "fim do arquivo" : "'" + t.lexeme() + "'";
    }
}
