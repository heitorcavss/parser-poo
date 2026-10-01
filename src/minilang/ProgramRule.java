package minilang;

/** programa -> 'inicio' declaracao* ifElse 'fim' EOF */
public class ProgramRule extends GrammarRule {

    @Override
    public boolean parse(TokenStream stream) {
        if (!expect(stream, TokenType.INICIO, "o código deve começar com 'inicio'")) {
            return false;
        }
        DeclarationRule declaration = new DeclarationRule();
        while (declaration.startsHere(stream)) {
            if (!declaration.parse(stream)) {
                return false;
            }
        }
        if (!stream.check(TokenType.IF)) {
            return fail(stream, "esperado o bloco 'if' (declarações devem vir antes e o 'if-else' é obrigatório), mas encontrado "
                    + stream.describeCurrent());
        }
        if (!new IfElseRule().parse(stream)) {
            return false;
        }
        if (stream.check(TokenType.IF)) {
            return fail(stream, "apenas um bloco if-else é permitido");
        }
        if (!expect(stream, TokenType.FIM, "esperado 'fim' para encerrar o código")) {
            return false;
        }
        return expect(stream, TokenType.EOF, "nenhum código é permitido após 'fim'");
    }
}
