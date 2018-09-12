package br.ufscar.dc.compilador.semantico;

import java.util.ArrayList;

public class TabelaDeSimbolos {
    private String escopo; // Nome do escopo referente a tabela
    private ArrayList<EntradaTabelaDeSimbolos> simbolos;

    public TabelaDeSimbolos(String escopo) {
        this.escopo = escopo;
        this.simbolos = new ArrayList<>();

    }

    public void adicionarSimbolo(String nome, String tipo, String tipoEntrada, ArrayList<String> listParam, TabelaDeSimbolos tabelaRegistro) {
        simbolos.add(new EntradaTabelaDeSimbolos(nome, tipo, tipoEntrada, listParam, tabelaRegistro));
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

    // Verifica se um determinado tipo existe na tabela
    boolean temTipo(String nome) {
        for (EntradaTabelaDeSimbolos tipo : simbolos) {
            if (tipo.equals(nome) && tipo.getTipo().equals(("tipo"))) {
                return true;
            }
        }
        return false;
    }

    // Retorna o tipo de um simbolo pelo nome
    public String getTipoPorNome(String nome) {
        for (EntradaTabelaDeSimbolos simbolo : simbolos) {
            if (simbolo.equals(nome)) {
                return simbolo.getTipo();
            }
        }
        return null;
    }
}
