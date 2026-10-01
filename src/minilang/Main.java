package minilang;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

/** Uso: java minilang.Main &lt; arquivo.mini */
public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        StringBuilder source = new StringBuilder();
        Scanner in = new Scanner(System.in, "UTF-8");
        while (in.hasNextLine()) {
            source.append(in.nextLine()).append('\n');
        }

        Lexer lexer = new Lexer(source.toString());
        ArrayList<Token> tokens = lexer.tokenize();
        if (lexer.hasError()) {
            System.err.println(lexer.getError());
            System.exit(1);
        }

        Parser parser = new Parser(tokens);
        if (parser.parse()) {
            System.out.println("OK: código sintaticamente correto.");
        } else {
            System.err.println(parser.getError());
            System.exit(1);
        }
    }
}
