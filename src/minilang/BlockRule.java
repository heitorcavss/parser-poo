package minilang;

/** bloco -> '{' comando* '}' */
public class BlockRule extends GrammarRule {
    // Array de Statement: a referência é do tipo da classe-mãe, os objetos são das filhas.
    private final Statement[] statements = {new AssignmentStatement(), new WhileStatement()};

    @Override
    public boolean parse(TokenStream stream) {
        if (!expect(stream, TokenType.LBRACE, "esperado '{' para abrir o bloco")) {
            return false;
        }
        while (!stream.check(TokenType.RBRACE)) {
            if (stream.check(TokenType.EOF) || stream.check(TokenType.FIM)) {
                return fail(stream, "bloco não fechado: esperado '}'");
            }
            Statement chosen = null;
            for (Statement s : statements) {
                if (s.startsHere(stream)) {
                    chosen = s;
                    break;
                }
            }
            if (chosen == null) {
                return fail(stream, "esperado comando (atribuição ou 'while'), mas encontrado "
                        + stream.describeCurrent());
            }
            if (!chosen.parse(stream)) { // polimorfismo: roda a versão da subclasse certa
                return false;
            }
        }
        stream.advance(); // }
        return true;
    }
}
