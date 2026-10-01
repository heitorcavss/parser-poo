package minilang;

/** fator -> NUMERO | ID | '(' expr ')' | '-' fator */
public class FactorRule extends GrammarRule {

    @Override
    public boolean parse(TokenStream stream) {
        if (stream.match(TokenType.NUMBER) || stream.match(TokenType.IDENTIFIER)) {
            return true;
        }
        if (stream.match(TokenType.MINUS)) {
            return parse(stream);
        }
        if (stream.match(TokenType.LPAREN)) {
            if (!new ExpressionRule().parse(stream)) {
                return false;
            }
            return expect(stream, TokenType.RPAREN, "esperado ')' para fechar a expressão");
        }
        return fail(stream, "esperado número, variável ou '(', mas encontrado " + stream.describeCurrent());
    }
}
