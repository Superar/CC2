package br.ufscar.dc;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.ufscar.dc.antlr.*;
import br.ufscar.dc.compilador.erros.*;
import br.ufscar.dc.compilador.gerador.GeradorDeCodigo;
import br.ufscar.dc.compilador.semantico.AnalisadorSemantico;

public final class Compilador {
    static PrintStream origout;
    static PrintStream fileout;

    private Compilador() {
    }

    public static void main(String[] args) {

        // Se segundo parâmetro for passado, redireciona saída para o arquivo
        if (args.length == 2) {
            try {
                origout = System.out;
                fileout = new PrintStream(args[1]);
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
                lexer.addErrorListener(ErrorListener.getInstance());

                CommonTokenStream tokens = new CommonTokenStream(lexer);

                // Analise sintatica
                ChronologicalParser parser = new ChronologicalParser(tokens);
                parser.removeErrorListeners();
                parser.addErrorListener(ErrorListener.getInstance());

                ChronologicalParser.CronogramasContext arvore = parser.cronogramas();

                // Analise semantica
                AnalisadorSemantico analisadorSemantico = new AnalisadorSemantico();
                analisadorSemantico.visitCronogramas(arvore);

                if (ErroSemantico.getInstance().temErros()) {
                    throw new ParseCancellationException(ErroSemantico.getInstance().toString());
                }

                // Geracao de codigo
                GeradorDeCodigo geradorDeCodigo = new GeradorDeCodigo();
                ParseTreeWalker walker = new ParseTreeWalker();
                walker.walk(geradorDeCodigo, arvore);
                System.out.println(geradorDeCodigo.getCodigo());
            } catch (IOException ex) {
                Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseCancellationException ex) { // Erro de compilacao lexico ou sintatico
                System.out.println(ex.getMessage());
                System.out.println("Fim da compilacao");
            }

            // Retorna o comportamento natural de System.out
            if (args.length == 2) {
                System.out.close();
                fileout.close();
                System.setOut(origout);
            }
        } else { // Nenhum ou mais de dois parametros foram fornecidos
            System.out.println("Parametros esperados:");
            System.out.println("Arquivo a ser compilado (obrigatorio)");
            System.out.println("Arquivo de saida (opcional)");
        }
    }
}
