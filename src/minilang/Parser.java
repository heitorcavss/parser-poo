package minilang;

import java.util.ArrayList;

/**
 * Analisador sintático da MiniLang. Recebe a lista de tokens e delega para a regra inicial;
 * cada regra da gramática é uma classe (veja ProgramRule, IfElseRule, BlockRule, ...).
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
public class Parser {
    private final TokenStream stream;
    private final Rule start = new ProgramRule();

    public Parser(ArrayList<Token> tokens) {
        this.stream = new TokenStream(tokens);
    }

    /** @return true se o código é sintaticamente correto; senão consulte {@link #getError()}. */
    public boolean parse() {
        return start.parse(stream);
    }

    public SyntaxError getError() {
        return stream.getError();
    }
}
