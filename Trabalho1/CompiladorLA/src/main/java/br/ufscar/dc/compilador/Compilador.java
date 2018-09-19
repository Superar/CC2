package br.ufscar.dc.compilador;

import br.ufscar.dc.compilador.erros.ErrorListener;
import br.ufscar.dc.compilador.gerador.GeradorDeCodigo;
import br.ufscar.dc.compilador.semantico.AnalisadorSemantico;

import org.antlr.v4.runtime.*;
import br.ufscar.dc.antlr.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Compilador {

    public static void main(String[] args) {

        // Se segundo parâmetro for passado, redireciona saída para o arquivo
        if (args.length == 2) {
            try {
                final PrintStream origout = System.out;
                final PrintStream fileout = new PrintStream(args[1]);
                System.setOut(new PrintStream(new OutputStream() {
                    @Override
                    public void write(int i) throws IOException {
                        origout.write(i);
                        fileout.write(i);
                    }
                }));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Ao menos um arquivo deve ser passado para ser compilado
        if (args.length == 1 || args.length == 2) {
            try {
                String caminho = args[0];
                CharStream input = CharStreams.fromFileName(caminho);

                // Analise lexica
                LALexer lexer = new LALexer(input);
                lexer.removeErrorListeners();
                lexer.addErrorListener(ErrorListener.INSTANCE);

                CommonTokenStream tokens = new CommonTokenStream(lexer);

                // Analise sintatica
                LAParser parser = new LAParser(tokens);
                parser.removeErrorListeners();
                parser.addErrorListener(ErrorListener.INSTANCE);

                // Analise semantica
                LAParser.ProgramaContext arvore = parser.programa();
                AnalisadorSemantico analisadorSemantico = new AnalisadorSemantico();
                analisadorSemantico.visitPrograma(arvore);
                if (analisadorSemantico.erros.temErros()) {
                    throw new ParseCancellationException(analisadorSemantico.erros.toString());
                }

                // Geracao de codigo
                GeradorDeCodigo gerador = new GeradorDeCodigo();
                ParseTreeWalker walker = new ParseTreeWalker();
                walker.walk(gerador, arvore);
                System.out.println(gerador.getCodigo());
            } catch (IOException ex) {
                Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseCancellationException ex) { // Erro de compilacao lexico ou sintatico
                System.out.println(ex.getMessage());
                System.out.println("Fim da compilacao");
            }
        } else { // Nenhum ou mais de dois parametros foram fornecidos
            System.out.println("Parametros esperados:");
            System.out.println("Arquivo a ser compilado (obrigatorio)");
            System.out.println("Arquivo de saida (opcional)");
        }
    }
}
