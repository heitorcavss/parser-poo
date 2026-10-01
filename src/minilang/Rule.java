package minilang;

/** Contrato de toda regra da gramática: tenta reconhecer a regra no fluxo de tokens. */
public interface Rule {
    /** @return true se reconheceu a regra; false se houve erro (já registrado no stream). */
    boolean parse(TokenStream stream);
}
