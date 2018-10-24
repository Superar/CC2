package br.ufscar.dc.compilador.semantico.tabela;

import java.util.ArrayList;
import java.util.Date;

class TabelaDeSimbolos {

    String cronograma;
    private ArrayList<EntradaTabelaDeSimbolos> simbolos;

    public TabelaDeSimbolos(String nome) {
        cronograma = nome;
        simbolos = new ArrayList<>();
    }

    public void adicionarSimbolo(String nome) {
        simbolos.add(new EntradaTabelaDeSimbolos(nome));
    }

    public boolean temSimbolo(String simbolo) {
        for (EntradaTabelaDeSimbolos s : simbolos) {
            if (s.nomeAtividade.equals(simbolo)) {
                return true;
            }
        }
        return false;
    }

    public void adicionarPeriodo(Date dataInicial, Date dataFinal) {
        simbolos.get(simbolos.size() - 1).adicionarPeriodo(dataInicial, dataFinal);
    }

    @Override
    public String toString() {
        return cronograma + ": " + simbolos.toString();
    }

}