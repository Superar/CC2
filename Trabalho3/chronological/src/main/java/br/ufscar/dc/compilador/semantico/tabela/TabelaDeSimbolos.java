package br.ufscar.dc.compilador.semantico.tabela;

import java.util.ArrayList;

class TabelaDeSimbolos {

    String cronograma;
    private ArrayList<String> simbolos;

    public TabelaDeSimbolos(String nome) {
        cronograma = nome;
        simbolos = new ArrayList<>();
    }

    public void adicionarSimbolo(String nome) {
        simbolos.add(nome);
    }

    public boolean temSimbolo(String simbolo) {
        for (String s : simbolos) {
            if (s.equals(simbolo)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return cronograma + ": " + simbolos.toString();
    }

}