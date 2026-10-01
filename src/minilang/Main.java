package minilang;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Uso: java minilang.Main arquivo.mini */
public class Main {
    public static void main(String[] args) throws Exception {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        Parser parser = new Parser(Lexer.tokenizar(Files.readString(Path.of(args[0]))));
        System.out.println(parser.parse() ? "OK: código sintaticamente correto." : parser.getErro());
    }
}
