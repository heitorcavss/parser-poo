package minilang;

/** condicao -> expr ( '==' | '!=' | '<' | '>' | '<=' | '>=' ) expr */
public class ConditionRule extends GrammarRule {
    private static final String[] RELATIONAL = {
        TokenType.EQ, TokenType.NEQ, TokenType.LT, TokenType.GT, TokenType.LE, TokenType.GE
    };

    @Override
    public boolean parse(TokenStream stream) {
        if (!new ExpressionRule().parse(stream)) {
            return false;
        }
        if (!stream.checkAny(RELATIONAL)) {
            return fail(stream, "esperado operador relacional (==, !=, <, >, <=, >=), mas encontrado "
                    + stream.describeCurrent());
        }
        stream.advance();
        return new ExpressionRule().parse(stream);
    }
}
