package br.ufscar.dc.compilador.semantico;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.ufscar.dc.antlr.ChronologicalBaseVisitor;
import br.ufscar.dc.antlr.ChronologicalParser.*;
import br.ufscar.dc.compilador.erros.ErroSemantico;
import br.ufscar.dc.compilador.utils.Configuracoes;

public class AnalisadorSemantico extends ChronologicalBaseVisitor<Void> {

    Configuracoes configuracaoCurCronograma;
    public final ErroSemantico erros = ErroSemantico.getInstance();

    @Override
    public Void visitCronograma(CronogramaContext ctx) {
        configuracaoCurCronograma = new Configuracoes(ctx.configuracao());
        return super.visitCronograma(ctx);
    }

    @Override
    public Void visitData_formato(Data_formatoContext ctx) {
        char sep1, sep2;
        if (ctx.LITTLE_ENDIAN() != null || ctx.MID_ENDIAN() != null) {
            sep1 = ctx.getText().charAt(2);
            sep2 = ctx.getText().charAt(5);
        } else {
            sep1 = ctx.getText().charAt(4);
            sep2 = ctx.getText().charAt(7);
        }

        if (sep1 != sep2) {
            erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": Separadores das datas devem ser iguais");
        }

        return super.visitData_formato(ctx);
    }

    @Override
    public Void visitPeriodo(PeriodoContext ctx) {
        if (!dataValida(ctx.dataInicio.getText())) {
            erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": Data " + ctx.dataInicio.getText()
                    + " nao esta conforme o padrao " + configuracaoCurCronograma.formatoData);
        }
        if (!dataValida(ctx.dataFinal.getText())) {
            erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": Data " + ctx.dataFinal.getText()
                    + " nao esta conforme o padrao " + configuracaoCurCronograma.formatoData);
        }

        if (!periodoValido(ctx.dataInicio.getText(), ctx.dataFinal.getText())) {
            erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": Periodo invalido");
        }
        return super.visitPeriodo(ctx);
    }

    public boolean dataValida(String data) {
        try {
            configuracaoCurCronograma.dataFormater.parse(data);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public boolean periodoValido(String dataInicial, String dataFinal) {
        Date data1, data2;
        try {
            data1 = configuracaoCurCronograma.dataFormater.parse(dataInicial);
            data2 = configuracaoCurCronograma.dataFormater.parse(dataFinal);
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