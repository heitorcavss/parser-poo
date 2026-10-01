package minilang;

public class Token {
    private final String tipo;   // "inicio", "if", "(", "==", ... ou ID, NUM, ERRO, EOF
    private final String texto;
    private final int linha;
    private final int coluna;

    public Token(String tipo, String texto, int linha, int coluna) {
        this.tipo = tipo;
        this.texto = texto;
        this.linha = linha;
        this.coluna = coluna;
    }

    public String getTipo() { return tipo; }
    public String getTexto() { return texto; }
    public int getLinha() { return linha; }
    public int getColuna() { return coluna; }
}
