package br.ufscar.dc.compilador.gerador;

import br.ufscar.dc.antlr.ChronologicalParser.ConfigsContext;
import br.ufscar.dc.antlr.ChronologicalParser.ConfiguracaoContext;

public class Configuracoes {

    public boolean linhasHorizontais;
    public boolean linhasVerticais;
    public String cor;
    public double alturaBarra;
    public String formatoData;
    public boolean mostrarDias;

    public Configuracoes(ConfiguracaoContext ctx) {
        // Valores padrao
        linhasHorizontais = false;
        linhasVerticais = false;
        cor = "000000";
        alturaBarra = 0.5;
        formatoData = "dd/mm/yyyy";
        mostrarDias = false;

        // Altera valores padrao conforme forem visitados
        parseConfig(ctx.config1);
        for (ConfigsContext config : ctx.listaConfig) {
            parseConfig(config);
        }
    }

    private void parseConfig(ConfigsContext ctx) {
        if (ctx.getText().equals("Linhas horizontais")) {
            linhasHorizontais = true;
        } else if (ctx.getText().equals("Linhas verticais")) {
            linhasVerticais = true;
        } else if (ctx.cor() != null) {
            cor = ctx.cor().getText();
        } else if (ctx.altura_barra() != null) {
            alturaBarra = Double.parseDouble(ctx.altura_barra().NUMERO_REAL().getText());
        } else if (ctx.DATA_FORMATO() != null) {
            formatoData = ctx.DATA_FORMATO().getText();
        } else if (ctx.getText().equals("Mostrar dias")) {
            mostrarDias = true;
        }
    }
}