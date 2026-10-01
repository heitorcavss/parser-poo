package minilang;

/** Classe abstrata com os utilitários comuns a todas as regras. Não pode ser instanciada. */
public abstract class GrammarRule implements Rule {

    /** Registra o erro e devolve false, para permitir {@code return fail(...)}. */
    protected boolean fail(TokenStream stream, String message) {
        stream.reportError(message);
        return false;
    }

    /** Consome um token do tipo esperado ou registra o erro "message, mas encontrado X". */
    protected boolean expect(TokenStream stream, String type, String message) {
        if (stream.check(type)) {
            stream.advance();
            return true;
        }
        return fail(stream, message + ", mas encontrado " + stream.describeCurrent());
    }
}
