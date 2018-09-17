package br.ufscar.dc.compilador.semantico;

import java.util.ArrayList;

public class EntradaTabelaDeSimbolos {
    private String nome;
    private String tipo;
    private String tipoEntrada; //Variavel, Função, Procedimento, Registro
    private ArrayList<String> listParam; // Lista de parametros da função
    private TabelaDeSimbolos tabelaRegistro; //Tabela de Simbolos para registros


    EntradaTabelaDeSimbolos(String nome, String tipo, String tipoEntrada, ArrayList<String> listParam, TabelaDeSimbolos tabelaRegistro) {
        this.nome = nome;
        this.tipo = tipo;
        this.tipoEntrada = tipoEntrada;
        this.listParam = listParam;
        this.tabelaRegistro = tabelaRegistro;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public String getTipoEntrada() { return tipoEntrada;}

    public ArrayList<String> getListParam() {return listParam;}

    public TabelaDeSimbolos getTabelaRegistro() {
        return tabelaRegistro;
    }

    boolean equals(String o) {
        return o.equals(this.nome);
    }
}
