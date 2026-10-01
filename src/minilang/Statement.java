package minilang;

/** Um comando dentro de um bloco. Cada tipo de comando é uma subclasse (polimorfismo). */
public abstract class Statement extends GrammarRule {

    /** Diz se o token atual pode iniciar este tipo de comando. */
    public abstract boolean startsHere(TokenStream stream);
}
