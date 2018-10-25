package br.ufscar.dc.compilador.semantico.tabela;

import java.util.ArrayList;
import java.util.Date;

public class EntradaTabelaDeSimbolos {
    String nomeAtividade;
    public ArrayList<Periodo> periodos;

    public EntradaTabelaDeSimbolos(String nome) {
        nomeAtividade = nome;
        periodos = new ArrayList<>();
    }

    public void adicionarPeriodo(Date dataInicial, Date dataFinal) {
        periodos.add(new Periodo(dataInicial, dataFinal));
    }

    public Date minData() {
        Date dataMinima = periodos.get(0).dataInicial;
        for (Periodo p : periodos) {
            if (p.dataInicial.before(dataMinima)) {
                dataMinima = p.dataInicial;
            }
        }
        return dataMinima;
    }

    public Date maxData() {
        Date dataMaxima = periodos.get(0).dataFinal;
        for (Periodo p : periodos) {
            if (p.dataFinal.after(dataMaxima)) {
                dataMaxima = p.dataFinal;
            }
        }
        return dataMaxima;
    }

    @Override
    public String toString() {
        return nomeAtividade + ": " + periodos.toString();
    }
}