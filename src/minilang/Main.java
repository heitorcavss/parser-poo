package minilang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Uso: java minilang.Main arquivo.mini
 * Sem argumento, lê o código da entrada padrão.
 */
public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        String source = args.length > 0 ? readFile(args[0]) : readAll(new Scanner(System.in, "UTF-8"));

        Lexer lexer = new Lexer(source);
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

    private static String readFile(String path) {
        try {
            return readAll(new Scanner(new File(path), "UTF-8"));
        } catch (FileNotFoundException e) {
            System.err.println("Arquivo não encontrado: " + path);
            System.exit(2);
            return null; // inalcançável: exigido pelo compilador
        }
    }

    private static String readAll(Scanner in) {
        StringBuilder sb = new StringBuilder();
        while (in.hasNextLine()) {
            sb.append(in.nextLine()).append('\n');
        }
        in.close();
        return sb.toString();
    }
}
