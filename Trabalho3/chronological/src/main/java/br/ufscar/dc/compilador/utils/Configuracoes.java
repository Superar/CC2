package br.ufscar.dc.compilador.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.ufscar.dc.antlr.ChronologicalParser.ConfigsContext;
import br.ufscar.dc.antlr.ChronologicalParser.ConfiguracaoContext;

public class Configuracoes {

    public boolean linhasHorizontais;
    public boolean linhasVerticais;
    public String cor;
    public String formatoData;
    public double alturaBarra;
    public boolean mostrarDias;

    private SimpleDateFormat dataFormater;

    public Configuracoes(ConfiguracaoContext ctx) {
        // Valores padrao
        linhasHorizontais = false;
        linhasVerticais = false;
        cor = "000000";
        alturaBarra = 0.5;
        formatoData = "dd/mm/yyyy";
        dataFormater = new SimpleDateFormat("dd/MM/yyyy");
        mostrarDias = false;

        // Altera valores padrao conforme forem visitados
        parseConfig(ctx.config1);
        if (ctx.listaConfig != null) {
            for (ConfigsContext config : ctx.listaConfig) {
                parseConfig(config);
            }
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
        } else if (ctx.data_formato() != null) {
            formatoData = ctx.data_formato().getText();
            dataFormater = new SimpleDateFormat(ctx.data_formato().getText().replace("m", "M"));
        } else if (ctx.getText().equals("Mostrar dias")) {
            mostrarDias = true;
        }
    }

    public boolean dataValida(String data) {
        try {
            dataFormater.parse(data);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public boolean periodoValido(String dataInicial, String dataFinal) {
        Date data1, data2;
        try {
            data1 = dataFormater.parse(dataInicial);
            data2 = dataFormater.parse(dataFinal);
        } catch (ParseException e) {
            // Retorna true, pois o erro das datas invalidas ja sera acusado
            // pelo metodo dataValida
            return true;
        }

        if (data1.before(data2) || data1.equals(data2)) {
            return true;
        } else {
            return false;
        }
    }
}