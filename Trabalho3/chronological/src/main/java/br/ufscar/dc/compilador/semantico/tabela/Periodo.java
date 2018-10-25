package br.ufscar.dc.compilador.semantico.tabela;

import java.util.Date;

public class Periodo {
    public Date dataInicial, dataFinal;

    public Periodo(Date dataInicial, Date dataFinal) {
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
    }

    @Override
    public String toString() {
        return dataInicial.toString() + " - " + dataFinal.toString();
    }
}