package br.ufscar.dc.compilador.erros;

import java.util.ArrayList;

public class ErroSemantico {
    private ArrayList<String> erros;

    public ErroSemantico() {
        erros = new ArrayList<String>();
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