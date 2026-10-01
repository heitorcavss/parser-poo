package minilang;

/** termo -> fator ( ( '*' | '/' ) fator )* */
public class TermRule extends GrammarRule {

    @Override
    public boolean parse(TokenStream stream) {
        if (!new FactorRule().parse(stream)) {
            return false;
        }
        while (stream.check(TokenType.STAR) || stream.check(TokenType.SLASH)) {
            stream.advance();
            if (!new FactorRule().parse(stream)) {
                return false;
            }
        }
        return true;
    }
}
