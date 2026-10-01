package minilang;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        if (args.length != 1) {
            System.err.println("Uso: java minilang.Main <arquivo.mini>");
            System.exit(2);
        }
        String source = Files.readString(Path.of(args[0]));
        try {
            List<Token> tokens = new Lexer(source).tokenize();
            new Parser(tokens).parse();
            System.out.println("OK: código sintaticamente correto.");
        } catch (SyntaxError e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
