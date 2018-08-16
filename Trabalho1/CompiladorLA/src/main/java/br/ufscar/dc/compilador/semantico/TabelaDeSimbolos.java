package br.ufscar.dc.compilador.semantico;

import java.util.ArrayList;

public class TabelaDeSimbolos {
    private String escopo; // Nome do escopo referente a tabela
    private ArrayList<EntradaTabelaDeSimbolos> simbolos;

    public TabelaDeSimbolos(String escopo) {
        this.escopo = escopo;
        this.simbolos = new ArrayList<>();
    }

    public void adicionarSimbolo(String nome, EntradaTabelaDeSimbolos.Tipo tipo) {
        simbolos.add(new EntradaTabelaDeSimbolos(nome, tipo));
    }

    // Verifica se um determinado simbolo existe na tabela
    boolean temSimbolo(String nome) {
        for (EntradaTabelaDeSimbolos simbolo : simbolos) {
            if (simbolo.equals(nome)) {
                return true;
            }
        }
        return false;
    }
}
