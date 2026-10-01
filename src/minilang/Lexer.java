package minilang;

import java.util.ArrayList;

/** Corta o código em tokens. O tipo de palavras reservadas e símbolos é o próprio texto. */
public class Lexer {
    private static final String RESERVADAS = " inicio fim int double if else while ";
    private static final String SIMBOLOS = " ( ) { } ; = + - * / == != < > <= >= ";

    public static ArrayList<Token> tokenizar(String s) {
        ArrayList<Token> tokens = new ArrayList<>();
        int i = 0, linha = 1, coluna = 1;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '\n') { linha++; coluna = 1; i++; continue; }
            if (Character.isWhitespace(c)) { coluna++; i++; continue; }

            int inicio = i;
            String tipo;
            if (Character.isLetter(c) || c == '_') {
                while (i < s.length() && (Character.isLetterOrDigit(s.charAt(i)) || s.charAt(i) == '_')) i++;
                tipo = RESERVADAS.contains(" " + s.substring(inicio, i) + " ") ? s.substring(inicio, i) : "ID";
            } else if (Character.isDigit(c)) {
                while (i < s.length() && (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.')) i++;
                tipo = "NUM";
            } else {
                i++;
                if (i < s.length() && s.charAt(i) == '=' && "=!<>".indexOf(c) >= 0) i++; // ==, !=, <=, >=
                tipo = SIMBOLOS.contains(" " + s.substring(inicio, i) + " ") ? s.substring(inicio, i) : "ERRO";
            }
            tokens.add(new Token(tipo, s.substring(inicio, i), linha, coluna));
            coluna += i - inicio;
        }
        tokens.add(new Token("EOF", "", linha, coluna));
        return tokens;
    }

    public static class Token {
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
}
