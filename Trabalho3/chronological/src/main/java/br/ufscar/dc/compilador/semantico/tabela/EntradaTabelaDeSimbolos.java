package br.ufscar.dc.compilador.semantico.tabela;

import java.util.ArrayList;
import java.util.Date;

public class EntradaTabelaDeSimbolos {
    String nomeAtividade;
    ArrayList<Periodo> periodos;

    public EntradaTabelaDeSimbolos(String nome) {
        nomeAtividade = nome;
        periodos = new ArrayList<>();
    }

    public void adicionarPeriodo(Date dataInicial, Date dataFinal) {
        periodos.add(new Periodo(dataInicial, dataFinal));
    }

    @Override
    public String toString() {
        return nomeAtividade + ": " + periodos.toString();
    }
}

class Periodo {
    Date dataInicial, dataFinal;

    public Periodo(Date dataInicial, Date dataFinal) {
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
    }

    @Override
    public String toString() {
        return dataInicial.toString() + " - " + dataFinal.toString();
    }
}