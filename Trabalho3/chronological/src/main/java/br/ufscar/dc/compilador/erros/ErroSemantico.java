package br.ufscar.dc.compilador.erros;

import java.util.ArrayList;

public final class ErroSemantico {
    private static final ErroSemantico INSTANCE = new ErroSemantico();

    private ArrayList<String> erros;

    private ErroSemantico() {
        erros = new ArrayList<String>();
    }

    public static ErroSemantico getInstance() {
        return INSTANCE;
    }

    public void adicionarErro(String mensagem) {
        erros.add(mensagem);
    }

    public boolean temErros() {
        return !erros.isEmpty();
    }

    @Override
    public String toString() {
        return String.join("\n", erros);
    }
}