package minilang;

/** while -> 'while' '(' condicao ')' bloco */
public class WhileStatement extends Statement {

    @Override
    public boolean startsHere(TokenStream stream) {
        return stream.check(TokenType.WHILE);
    }

    @Override
    public boolean parse(TokenStream stream) {
        stream.advance(); // while
        if (!expect(stream, TokenType.LPAREN, "esperado '(' após 'while'")) {
            return false;
        }
        if (!new ConditionRule().parse(stream)) {
            return false;
        }
        if (!expect(stream, TokenType.RPAREN, "esperado ')' ao fechar a condição do 'while'")) {
            return false;
        }
        // BlockRule é criado só aqui (e não no construtor) para não gerar recursão infinita:
        // BlockRule cria WhileStatement, que cria BlockRule...
        return new BlockRule().parse(stream);
    }
}
