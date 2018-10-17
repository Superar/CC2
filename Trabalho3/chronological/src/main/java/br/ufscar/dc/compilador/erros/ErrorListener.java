package br.ufscar.dc.compilador.erros;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public class ErrorListener extends BaseErrorListener {
    public static final ErrorListener INSTANCE = new ErrorListener();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) throws ParseCancellationException {
        if (offendingSymbol == null) {
            int offset = "token recognition error at: '".length(); // Encontra a posicao do caractere nao identificado
            char s = msg.charAt(offset);

            throw new ParseCancellationException("Linha " + line + ": simbolo " + s + " nao identificado");

        } else {
            Token tok = (Token) offendingSymbol;
            throw new ParseCancellationException("Linha " + line + ": erro sintatico proximo a " + tok.getText());
        }
    }
}