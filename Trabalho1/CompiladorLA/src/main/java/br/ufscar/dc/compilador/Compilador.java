package br.ufscar.dc.compilador;

import org.antlr.v4.runtime.*;
import br.ufscar.dc.antlr.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Compilador {

    public static void main(String[] args) {
        FileInputStream caminho;

        try {
            caminho = new FileInputStream(args[0]);
            ANTLRInputStream input = new ANTLRInputStream(caminho);
            LALexer lexer = new LALexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LAParser parser = new LAParser(tokens);
            LAParser.ProgramaContext arvore = parser.programa();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
