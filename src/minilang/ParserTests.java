package minilang;

/** Testes simples sem dependências externas: rode a main (no IntelliJ, botão ▶ ao lado da classe). */
public class ParserTests {
    private static int failures = 0;

    public static void main(String[] args) {
        System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
        ok("minimo", "inicio if (1 < 2) { } else { } fim");
        ok("completo", """
                inicio
                  int a = 10;
                  double b;
                  int i = 0;
                  if (a >= (b + 1) * 2) {
                    a = -a / 2;
                    while (i != 3) { i = i + 1; }
                  } else {
                    b = 1.5;
                  }
                fim
                """);

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
        if (failures > 0) System.exit(1);
    }

    private static void ok(String name, String src) {
        try {
            new Parser(new Lexer(src).tokenize()).parse();
            System.out.println("PASS  " + name);
        } catch (SyntaxError e) {
            fail(name, "esperava aceitar, mas: " + e.getMessage());
        }
    }

    private static void err(String name, String src, int line, int col, String msgPart) {
        try {
            new Parser(new Lexer(src).tokenize()).parse();
            fail(name, "esperava erro, mas foi aceito");
        } catch (SyntaxError e) {
            if (e.getLine() == line && e.getColumn() == col && e.getMessage().contains(msgPart)) {
                System.out.println("PASS  " + name + "  ->  " + e.getMessage());
            } else {
                fail(name, "esperado " + line + ":" + col + " contendo '" + msgPart + "', obtido: " + e.getMessage());
            }
        }
    }

    private static void fail(String name, String why) {
        failures++;
        System.out.println("FAIL  " + name + "  ->  " + why);
    }
}
