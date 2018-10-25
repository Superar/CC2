package br.ufscar.dc.compilador.semantico.tabela;

import java.util.ArrayList;
import java.util.Date;

public final class ListaDeTabelas {

    private static final ListaDeTabelas INSTANCE = new ListaDeTabelas();

    private ArrayList<TabelaDeSimbolos> tabelas;

    private ListaDeTabelas() {
        tabelas = new ArrayList<>();
    }

    public static ListaDeTabelas getInstance() {
        return INSTANCE;
    }

    public void criarTabela(String nome) {
        tabelas.add(new TabelaDeSimbolos(nome));
    }

    public void adicionarSimbolo(String nomeTabela, String simbolo) {
        TabelaDeSimbolos tabela = getTabela(nomeTabela);
        tabela.adicionarSimbolo(simbolo);
    }

    public void adicionarPeriodo(String nomeTabela, Date dataInicial, Date dataFinal) {
        TabelaDeSimbolos tabela = getTabela(nomeTabela);
        tabela.adicionarPeriodo(dataInicial, dataFinal);
    }

    public boolean temTabela(String nome) {
        if (getTabela(nome) != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean temSimbolo(String nomeTabela, String simbolo) {
        return getTabela(nomeTabela).temSimbolo(simbolo);
    }

    public Date dataInicialDeCronograma(String nomeTabela) {
        return getTabela(nomeTabela).minData();
    }

    public Date dataFinalDeCronograma(String nomeTabela) {
        return getTabela(nomeTabela).maxData();
    }

    public ArrayList<Periodo> getPeriodosDeAtividade(String nomeTabela, String nomeAtividade) {
        return getTabela(nomeTabela).getPeriodosDeAtividade(nomeAtividade);
    }

    private TabelaDeSimbolos getTabela(String nome) {
        for (TabelaDeSimbolos tabela : tabelas) {
            if (tabela.cronograma.equals(nome)) {
                return tabela;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return tabelas.toString();
    }
}