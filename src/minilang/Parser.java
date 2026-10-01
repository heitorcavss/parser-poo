package minilang;

import minilang.Lexer.Token;

import java.util.ArrayList;

/**
 * programa   -> inicio declaracao* ifElse fim
 * declaracao -> (int | double) ID ('=' expr)? ';'
 * ifElse     -> if '(' condicao ')' bloco else bloco
 * bloco      -> '{' atribuicao* '}'
 * atribuicao -> ID '=' expr ';'
 * condicao   -> expr (== | != | < | > | <= | >=) expr
 * expr       -> termo ((+ | -) termo)*
 * termo      -> fator ((* | /) fator)*
 * fator      -> NUM | ID | '(' expr ')' | '-' fator
 *
 * Cada método devolve true se reconheceu a regra e false se achou erro (guardado em getErro()).
 */
public class Parser {
    private final ArrayList<Token> tokens;
    private int pos = 0;
    private String erro = null;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public boolean parse() { return programa(); }

    public String getErro() { return erro; }

    private boolean programa() {
        if (!consome("inicio")) return false;
        while (ve("int") || ve("double")) {
            if (!declaracao()) return false;
        }
        return ifElse() && consome("fim") && consome("EOF");
    }

    private boolean declaracao() {
        pos++; // int | double
        if (!consome("ID")) return false;
        if (ve("=")) {
            pos++;
            if (!expr()) return false;
        }
        return consome(";");
    }

    private boolean ifElse() {
        return consome("if") && consome("(") && condicao() && consome(")")
                && bloco() && consome("else") && bloco();
    }

    private boolean bloco() {
        if (!consome("{")) return false;
        while (!ve("}")) {
            if (!atribuicao()) return false;
        }
        return consome("}");
    }

    private boolean atribuicao() {
        return consome("ID") && consome("=") && expr() && consome(";");
    }

    private boolean condicao() {
        if (!expr()) return false;
        if (!" == != < > <= >= ".contains(" " + tipo() + " ")) {
            return falha("esperado operador relacional (==, !=, <, >, <=, >=), mas encontrado " + atual());
        }
        pos++;
        return expr();
    }

    private boolean expr() {
        if (!termo()) return false;
        while (ve("+") || ve("-")) {
            pos++;
            if (!termo()) return false;
        }
        return true;
    }

    private boolean termo() {
        if (!fator()) return false;
        while (ve("*") || ve("/")) {
            pos++;
            if (!fator()) return false;
        }
        return true;
    }

    private boolean fator() {
        if (ve("NUM") || ve("ID")) { pos++; return true; }
        if (ve("-")) { pos++; return fator(); }
        if (ve("(")) { pos++; return expr() && consome(")"); }
        return falha("esperado número, variável ou '(', mas encontrado " + atual());
    }

    // ---------- utilitários ----------

    private String tipo() { return tokens.get(pos).getTipo(); }

    private boolean ve(String tipo) { return tipo().equals(tipo); }

    private boolean consome(String tipo) {
        if (ve(tipo)) {
            if (!tipo.equals("EOF")) pos++;
            return true;
        }
        return falha("esperado " + nome(tipo) + ", mas encontrado " + atual());
    }

    private boolean falha(String msg) {
        if (erro == null) {
            Token t = tokens.get(pos);
            erro = "Erro sintático [linha " + t.getLinha() + ", coluna " + t.getColuna() + "]: " + msg;
        }
        return false;
    }

    private String atual() {
        return tipo().equals("EOF") ? "fim do arquivo" : "'" + tokens.get(pos).getTexto() + "'";
    }

    private String nome(String tipo) {
        if (tipo.equals("ID")) return "identificador";
        if (tipo.equals("NUM")) return "número";
        if (tipo.equals("EOF")) return "fim do arquivo";
        return "'" + tipo + "'";
    }
}
