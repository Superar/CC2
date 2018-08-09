package br.ufscar.dc.compilador;

import org.antlr.v4.runtime.*;
import br.ufscar.dc.antlr.*;

public class Compilador {

    public static void main(String[] args) {
        String teste = "a = b + c";
        ANTLRInputStream input = new ANTLRInputStream(teste);
        LALexer lexer = new LALexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LAParser parser = new LAParser(tokens);
        LAParser.ProgramaContext arvore = parser.programa();
        System.out.println(arvore.start);
    }
}
