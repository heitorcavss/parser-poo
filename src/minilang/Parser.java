package minilang;

import java.util.ArrayList;

/**
 * Analisador sintático da MiniLang. Recebe a lista de tokens e valida o programa inteiro.
 * Expressões, blocos e comandos ficam em suas próprias classes (ExpressionRule, BlockRule, Statement...).
 *
 * <pre>
 * programa   -> inicio declaracao* ifElse fim
 * declaracao -> (int|double) ID ('=' expr)? ';'
 * ifElse     -> if '(' condicao ')' bloco else bloco
 * bloco      -> '{' comando* '}'
 * comando    -> atribuicao | while
 * atribuicao -> ID '=' expr ';'
 * while      -> while '(' condicao ')' bloco
 * condicao   -> expr (== != &lt; &gt; &lt;= &gt;=) expr
 * expr       -> termo ((+|-) termo)*
 * termo      -> fator ((*|/) fator)*
 * fator      -> NUMERO | ID | '(' expr ')' | '-' fator
 * </pre>
 */
public class Parser extends GrammarRule {
    private final TokenStream stream;

    public Parser(ArrayList<Token> tokens) {
        this.stream = new TokenStream(tokens);
    }

    /** @return true se o código é sintaticamente correto; senão consulte {@link #getError()}. */
    public boolean parse() {
        return parse(stream);
    }

    public SyntaxError getError() {
        return stream.getError();
    }

    /** programa -> 'inicio' declaracao* ifElse 'fim' EOF */
    @Override
    public boolean parse(TokenStream s) {
        if (!expect(s, TokenType.INICIO, "o código deve começar com 'inicio'")) {
            return false;
        }
        while (s.check(TokenType.INT) || s.check(TokenType.DOUBLE)) {
            if (!declaration(s)) {
                return false;
            }
        }
        if (!s.check(TokenType.IF)) {
            return fail(s, "esperado o bloco 'if' (declarações devem vir antes e o 'if-else' é obrigatório), mas encontrado "
                    + s.describeCurrent());
        }
        if (!ifElse(s)) {
            return false;
        }
        if (s.check(TokenType.IF)) {
            return fail(s, "apenas um bloco if-else é permitido");
        }
        if (!expect(s, TokenType.FIM, "esperado 'fim' para encerrar o código")) {
            return false;
        }
        return expect(s, TokenType.EOF, "nenhum código é permitido após 'fim'");
    }

    /** declaracao -> ( 'int' | 'double' ) ID ( '=' expr )? ';' */
    private boolean declaration(TokenStream s) {
        s.advance(); // int | double
        if (!expect(s, TokenType.IDENTIFIER, "esperado nome da variável após o tipo")) {
            return false;
        }
        if (s.match(TokenType.ASSIGN) && !new ExpressionRule().parse(s)) {
            return false;
        }
        return expect(s, TokenType.SEMICOLON, "esperado ';' ao final da declaração");
    }

    /** ifElse -> 'if' '(' condicao ')' bloco 'else' bloco */
    private boolean ifElse(TokenStream s) {
        s.advance(); // if
        if (!expect(s, TokenType.LPAREN, "esperado '(' após 'if'")) {
            return false;
        }
        if (!new ExpressionRule().parseCondition(s)) {
            return false;
        }
        if (!expect(s, TokenType.RPAREN, "esperado ')' ao fechar a condição do 'if'")) {
            return false;
        }
        if (!new BlockRule().parse(s)) {
            return false;
        }
        if (!expect(s, TokenType.ELSE, "esperado 'else' (o bloco if-else exige a cláusula else)")) {
            return false;
        }
        return new BlockRule().parse(s);
    }
}
