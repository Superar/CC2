package br.ufscar.dc.compilador.semantico;

import java.util.Stack;

public class PilhaDeTabelas {

    Stack<TabelaDeSimbolos> pilha;

    PilhaDeTabelas() {
        pilha = new Stack<>();
    }

    // Verifica se um determinado simbolo existe
    // em qualquer tabela da pilha
    public boolean temSimbolo(String nome) {
        for (TabelaDeSimbolos tabela : this.pilha) {
            if (tabela.temSimbolo(nome)) {
                return true;
            }
        }
        return false;
    }

    // Verifica se um determinado tipo existe
    // em qualquer tabela da pilha
    public boolean temTipo(String nome) {
        for (TabelaDeSimbolos tabela : this.pilha) {
            if (tabela.temTipo(nome)) {
                return true;
            }
        }
        return false;
    }

    // Retorna o tipo de um determinado simbolo
    public String getTipoPorNome(String nome) {
        for (TabelaDeSimbolos tabela : this.pilha) {
            if (tabela.temSimbolo(nome)) {
                return tabela.getTipoPorNome(nome);
            }
        }
        return null;
    }

    // Retorna a entrada da tabela correspondente ao nome do simbolo
    public EntradaTabelaDeSimbolos getEntradaPorNome(String nome) {
        EntradaTabelaDeSimbolos ret = null;
        for (TabelaDeSimbolos tabela : this.pilha) {
            ret = tabela.getEntradaPorNome(nome);
            if (ret != null) {
                return ret;
            }
        }
        return ret;
    }

    public TabelaDeSimbolos topo() {
        return this.pilha.peek();
    }

    public void empilha(TabelaDeSimbolos e) {
        this.pilha.push(e);
    }

    public void desempilha() {
        this.pilha.pop();
    }
}
