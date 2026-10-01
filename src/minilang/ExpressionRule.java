package minilang;

/**
 * Expressões e condições:
 * <pre>
 * condicao -> expr ( '==' | '!=' | '<' | '>' | '<=' | '>=' ) expr
 * expr     -> termo ( ( '+' | '-' ) termo )*
 * termo    -> fator ( ( '*' | '/' ) fator )*
 * fator    -> NUMERO | ID | '(' expr ')' | '-' fator
 * </pre>
 * {@link #parse} reconhece uma expressão; {@link #parseCondition} reconhece uma condição.
 */
public class ExpressionRule extends GrammarRule {
    private static final String[] RELATIONAL = {
        TokenType.EQ, TokenType.NEQ, TokenType.LT, TokenType.GT, TokenType.LE, TokenType.GE
    };

    @Override
    public boolean parse(TokenStream stream) {
        if (!term(stream)) {
            return false;
        }
        while (stream.check(TokenType.PLUS) || stream.check(TokenType.MINUS)) {
            stream.advance();
            if (!term(stream)) {
                return false;
            }
        }
        return true;
    }

    public boolean parseCondition(TokenStream stream) {
        if (!parse(stream)) {
            return false;
        }
        if (!stream.checkAny(RELATIONAL)) {
            return fail(stream, "esperado operador relacional (==, !=, <, >, <=, >=), mas encontrado "
                    + stream.describeCurrent());
        }
        stream.advance();
        return parse(stream);
    }

    private boolean term(TokenStream stream) {
        if (!factor(stream)) {
            return false;
        }
        while (stream.check(TokenType.STAR) || stream.check(TokenType.SLASH)) {
            stream.advance();
            if (!factor(stream)) {
                return false;
            }
        }
        return true;
    }

    private boolean factor(TokenStream stream) {
        if (stream.match(TokenType.NUMBER) || stream.match(TokenType.IDENTIFIER)) {
            return true;
        }
        if (stream.match(TokenType.MINUS)) {
            return factor(stream);
        }
        if (stream.match(TokenType.LPAREN)) {
            if (!parse(stream)) {
                return false;
            }
            return expect(stream, TokenType.RPAREN, "esperado ')' para fechar a expressão");
        }
        return fail(stream, "esperado número, variável ou '(', mas encontrado " + stream.describeCurrent());
    }
}
