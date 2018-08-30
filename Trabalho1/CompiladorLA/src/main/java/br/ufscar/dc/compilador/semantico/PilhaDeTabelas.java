package br.ufscar.dc.compilador.semantico;

import java.util.Stack;

public class PilhaDeTabelas extends Stack<TabelaDeSimbolos> {

    // Verifica se um determinado simbolo existe
    // em qualquer tabela da pilha
    public boolean temSimbolo(String nome) {
        for (TabelaDeSimbolos tabela : this) {
            if (tabela.temSimbolo(nome)) {
                return true;
            }
        }
        return false;
    }

    // Verifica se um determinado tipo existe
    // em qualquer tabela da pilha
    public boolean temTipo(String nome) {
        for (TabelaDeSimbolos tabela : this) {
            if (tabela.temTipo(nome)) {
                return true;
            }
        }
        return false;
    }

    // Retorna o tipo de um determinado simbolo
    public String getTipoPorNome(String nome) {
        for (TabelaDeSimbolos tabela : this) {
            if (tabela.temSimbolo(nome)) {
                return tabela.getTipoPorNome(nome);
            }
        }
        return null;
    }

    public TabelaDeSimbolos topo() {
        return this.peek();
    }

    public void empilha(TabelaDeSimbolos e) {
        this.push(e);
    }

    public void desempilha() {
        this.pop();
    }
}
