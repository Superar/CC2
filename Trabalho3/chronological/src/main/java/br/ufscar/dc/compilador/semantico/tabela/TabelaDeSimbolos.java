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

    public Date minData() {
        Date dataMinima = simbolos.get(0).minData();
        for (EntradaTabelaDeSimbolos s : simbolos) {
            if (s.minData().before(dataMinima)) {
                dataMinima = s.minData();
            }
        }
        return dataMinima;
    }

    public Date maxData() {
        Date dataMaxima = simbolos.get(0).maxData();
        for (EntradaTabelaDeSimbolos s : simbolos) {
            if (s.maxData().after(dataMaxima)) {
                dataMaxima = s.maxData();
            }
        }
        return dataMaxima;
    }

    ArrayList<Periodo> getPeriodosDeAtividade(String nomeAtividade) {
        return getAtividade(nomeAtividade).periodos;
    }

    public EntradaTabelaDeSimbolos getAtividade(String nomeAtividade) {
        for (EntradaTabelaDeSimbolos s : simbolos) {
            if (s.nomeAtividade.equals(nomeAtividade)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return cronograma + ": " + simbolos.toString();
    }

}