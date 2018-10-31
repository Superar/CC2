package br.ufscar.dc.compilador.utils;

import java.text.SimpleDateFormat;

import br.ufscar.dc.antlr.ChronologicalParser.ConfigsContext;
import br.ufscar.dc.antlr.ChronologicalParser.ConfiguracaoContext;

public class Configuracoes {

    public boolean linhasHorizontais;
    public boolean linhasVerticais;
    public String esquemaDeCores;
    public String cor;
    public String formatoData;
    public double alturaBarra;
    public boolean mostrarDias;

    public SimpleDateFormat dataFormater;

    public boolean corAlterada, alturaBarraAlterada;

    public Configuracoes(ConfiguracaoContext ctx) {
        this();

        // Altera valores padrao conforme forem visitados
        parseConfig(ctx.config1);
        if (ctx.listaConfig != null) {
            for (ConfigsContext config : ctx.listaConfig) {
                parseConfig(config);
            }
        }
    }

    public Configuracoes() {
        // Valores padrao
        linhasHorizontais = false;
        linhasVerticais = false;
        esquemaDeCores = "HTML";
        cor = "FFFFFF";
        alturaBarra = 0.5;
        formatoData = "dd/mm/yyyy";
        dataFormater = new SimpleDateFormat("dd/MM/yyyy");
        mostrarDias = false;

        corAlterada = false;
        alturaBarraAlterada = false;
    }

    private void parseConfig(ConfigsContext ctx) {
        if (ctx.getText().equals("Linhas horizontais")) {
            linhasHorizontais = true;
        } else if (ctx.getText().equals("Linhas verticais")) {
            linhasVerticais = true;
        } else if (ctx.cor() != null) {
            corAlterada = true;
            if (ctx.cor().cor_rgb() != null) {
                esquemaDeCores = "RGB";
                // Ignora () no inicio e fim da definicao da cor
                String text = ctx.cor().cor_rgb().getText();
                cor = text.substring(1, text.length() - 1);
            } else {
                esquemaDeCores = "HTML";
                // Ignora o 0x no inicio da deficinao de cor
                String text = ctx.cor().cor_hex().getText();
                cor = text.substring(2, text.length());
            }
        } else if (ctx.altura_barra() != null) {
            alturaBarra = Double.parseDouble(ctx.altura_barra().NUMERO_REAL().getText());
            alturaBarraAlterada = true;
        } else if (ctx.data_formato() != null) {
            formatoData = ctx.data_formato().getText();
            dataFormater = new SimpleDateFormat(ctx.data_formato().getText().replace("m", "M"));
        } else if (ctx.getText().equals("Mostrar dias")) {
            mostrarDias = true;
        }
    }
}