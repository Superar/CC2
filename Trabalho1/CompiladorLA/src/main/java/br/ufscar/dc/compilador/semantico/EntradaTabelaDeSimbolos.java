package br.ufscar.dc.compilador.semantico;

public class EntradaTabelaDeSimbolos {
    public enum Tipo {INT, REAL, LITERAL}

    ;
    private String nome;
    private Tipo tipo;

    EntradaTabelaDeSimbolos(String nome, Tipo tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo.name();
    }

    boolean equals(String o) {
        return o.equals(this.nome);
    }
}
