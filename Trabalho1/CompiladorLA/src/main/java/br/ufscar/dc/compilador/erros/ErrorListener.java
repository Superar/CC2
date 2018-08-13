package br.ufscar.dc.compilador.erros;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public class ErrorListener extends BaseErrorListener {
    public static final ErrorListener INSTANCE = new ErrorListener();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object simbolo, int linha, int coluna, String msg, RecognitionException e) throws ParseCancellationException {
        if (simbolo == null) { // Erro lexico
            int offset = "token recognition error at: '".length(); // Encontra a posicao do caractere nao identificado
            throw new ParseCancellationException("Linha " + linha + ": " + msg.charAt(offset) + " - simbolo nao identificado");
        } else { // Erro sintatico
            Token tok = (Token) simbolo;
            if (tok.getText().equals("<EOF>")) {
                throw new ParseCancellationException("Linha " + linha + ": erro sintatico proximo a EOF");
            } else {
                throw new ParseCancellationException("Linha " + linha + ": erro sintatico proximo a " + tok.getText());
            }
        }
    }
}
