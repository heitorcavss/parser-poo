package minilang;

/** ifElse -> 'if' '(' condicao ')' bloco 'else' bloco */
public class IfElseRule extends GrammarRule {

    @Override
    public boolean parse(TokenStream stream) {
        if (!expect(stream, TokenType.IF, "esperado 'if'")) {
            return false;
        }
        if (!expect(stream, TokenType.LPAREN, "esperado '(' após 'if'")) {
            return false;
        }
        if (!new ConditionRule().parse(stream)) {
            return false;
        }
        if (!expect(stream, TokenType.RPAREN, "esperado ')' ao fechar a condição do 'if'")) {
            return false;
        }
        if (!new BlockRule().parse(stream)) {
            return false;
        }
        if (!expect(stream, TokenType.ELSE, "esperado 'else' (o bloco if-else exige a cláusula else)")) {
            return false;
        }
        return new BlockRule().parse(stream);
    }
}
