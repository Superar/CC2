package br.ufscar.dc;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.ufscar.dc.antlr.*;
import br.ufscar.dc.compilador.erros.ErrorListener;

public final class Compilador {
    private Compilador() {
    }

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
                ChronologicalLexer lexer = new ChronologicalLexer(input);
                lexer.removeErrorListeners();
                lexer.addErrorListener(ErrorListener.INSTANCE);

                CommonTokenStream tokens = new CommonTokenStream(lexer);

                // Analise sintatica
                ChronologicalParser parser = new ChronologicalParser(tokens);
                parser.removeErrorListeners();
                parser.addErrorListener(ErrorListener.INSTANCE);

                ChronologicalParser.CronogramasContext arvore = parser.cronogramas();
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
