package br.ufscar.dc.compilador.semantico;

public class EntradaTabelaDeSimbolos {
    private String nome;
    private String tipo;

    EntradaTabelaDeSimbolos(String nome, String tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    boolean equals(String o) {
        return o.equals(this.nome);
    }
}
