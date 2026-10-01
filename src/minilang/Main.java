package minilang;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Uso: java minilang.Main arquivo.mini */
public class Main {
    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        String codigo = Files.readString(Path.of(args[0]));
        Parser parser = new Parser(Lexer.tokenizar(codigo));
        if (parser.parse()) {
            System.out.println("OK: código sintaticamente correto.");
        } else {
            System.out.println(parser.getErro());
        }
    }
}
