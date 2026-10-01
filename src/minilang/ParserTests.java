package minilang;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/** Testes simples sem dependências externas: rode a main (no IntelliJ, botão ▶ ao lado da classe). */
public class ParserTests {
    private static int failures = 0;

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        ok("minimo", "inicio if (1 < 2) { } else { } fim");
        ok("completo", "inicio\n"
                + "  int a = 10;\n"
                + "  double b;\n"
                + "  int i = 0;\n"
                + "  if (a >= (b + 1) * 2) {\n"
                + "    a = -a / 2;\n"
                + "    i = i + 1;\n"
                + "  } else {\n"
                + "    b = 1.5;\n"
                + "  }\n"
                + "fim\n");

        err("while fora da gramática", "inicio if (1<2) { while (1<2) {} } else {} fim", 1, 19, "esperado atribuição");
        err("sem inicio", "if (1<2) {} else {} fim", 1, 1, "inicio");
        err("sem fim", "inicio if (1<2) {} else {}", 1, 27, "fim");
        err("sem else", "inicio if (1<2) {} fim", 1, 20, "else");
        err("sem ;", "inicio int a = 1\n if (a<2) {} else {} fim", 2, 2, "';'");
        err("sem operador relacional", "inicio int a; if (a) {} else {} fim", 1, 20, "relacional");
        err("dois if-else", "inicio if (1<2) {} else {} if (1<2) {} else {} fim", 1, 28, "apenas um");
        err("declaracao depois do if", "inicio if (1<2) {} else {} int a; fim", 1, 28, "fim");
        err("bloco aberto", "inicio if (1<2) { a = 1; fim", 1, 26, "não fechado");
        err("expressao incompleta", "inicio int a = 1 + ; if (1<2) {} else {} fim", 1, 20, "esperado número");
        err("lexico: caractere", "inicio int a = 1 @ ;", 1, 18, "inesperado");
        err("lexico: ! isolado", "inicio if (a ! b) {} else {} fim", 1, 14, "!=");
        err("lixo apos fim", "inicio if (1<2) {} else {} fim int", 1, 32, "após 'fim'");

        System.out.println(failures == 0 ? "\nTodos os testes passaram ✅" : "\n" + failures + " teste(s) falharam ❌");
        if (failures > 0) {
            System.exit(1);
        }
    }

    /** Roda Lexer + Parser e devolve o primeiro erro, ou null se o código é válido. */
    private static SyntaxError run(String src) {
        Lexer lexer = new Lexer(src);
        ArrayList<Token> tokens = lexer.tokenize();
        if (lexer.hasError()) {
            return lexer.getError();
        }
        Parser parser = new Parser(tokens);
        return parser.parse() ? null : parser.getError();
    }

    private static void ok(String name, String src) {
        SyntaxError e = run(src);
        if (e == null) {
            System.out.println("PASS  " + name);
        } else {
            fail(name, "esperava aceitar, mas: " + e);
        }
    }

    private static void err(String name, String src, int line, int col, String msgPart) {
        SyntaxError e = run(src);
        if (e == null) {
            fail(name, "esperava erro, mas foi aceito");
        } else if (e.getLine() == line && e.getColumn() == col && e.getMessage().contains(msgPart)) {
            System.out.println("PASS  " + name + "  ->  " + e);
        } else {
            fail(name, "esperado " + line + ":" + col + " contendo '" + msgPart + "', obtido: " + e);
        }
    }

    private static void fail(String name, String why) {
        failures++;
        System.out.println("FAIL  " + name + "  ->  " + why);
    }
}
