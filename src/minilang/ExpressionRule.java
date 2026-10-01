package minilang;

/** expr -> termo ( ( '+' | '-' ) termo )* */
public class ExpressionRule extends GrammarRule {

    @Override
    public boolean parse(TokenStream stream) {
        if (!new TermRule().parse(stream)) {
            return false;
        }
        while (stream.check(TokenType.PLUS) || stream.check(TokenType.MINUS)) {
            stream.advance();
            if (!new TermRule().parse(stream)) {
                return false;
            }
        }
        return true;
    }
}
