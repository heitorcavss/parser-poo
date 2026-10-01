package minilang;

/** declaracao -> ( 'int' | 'double' ) ID ( '=' expr )? ';' */
public class DeclarationRule extends GrammarRule {

    public boolean startsHere(TokenStream stream) {
        return stream.check(TokenType.INT) || stream.check(TokenType.DOUBLE);
    }

    @Override
    public boolean parse(TokenStream stream) {
        stream.advance(); // int | double
        if (!expect(stream, TokenType.IDENTIFIER, "esperado nome da variável após o tipo")) {
            return false;
        }
        if (stream.match(TokenType.ASSIGN) && !new ExpressionRule().parse(stream)) {
            return false;
        }
        return expect(stream, TokenType.SEMICOLON, "esperado ';' ao final da declaração");
    }
}
