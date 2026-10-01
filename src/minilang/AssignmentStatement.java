package minilang;

/** atribuicao -> ID '=' expr ';' */
public class AssignmentStatement extends Statement {

    @Override
    public boolean startsHere(TokenStream stream) {
        return stream.check(TokenType.IDENTIFIER);
    }

    @Override
    public boolean parse(TokenStream stream) {
        stream.advance(); // ID
        if (!expect(stream, TokenType.ASSIGN, "esperado '=' na atribuição")) {
            return false;
        }
        if (!new ExpressionRule().parse(stream)) {
            return false;
        }
        return expect(stream, TokenType.SEMICOLON, "esperado ';' ao final da atribuição");
    }
}
