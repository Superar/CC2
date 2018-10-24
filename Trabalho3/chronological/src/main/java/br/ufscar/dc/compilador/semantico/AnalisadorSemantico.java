package br.ufscar.dc.compilador.semantico;

import java.text.ParseException;

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
        if (!configuracaoCurCronograma.dataValida(ctx.dataInicio.getText())) {
            erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": Data " + ctx.dataInicio.getText()
                    + " nao esta conforme o padrao " + configuracaoCurCronograma.formatoData);
        }
        if (!configuracaoCurCronograma.dataValida(ctx.dataFinal.getText())) {
            erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": Data " + ctx.dataFinal.getText()
                    + " nao esta conforme o padrao " + configuracaoCurCronograma.formatoData);
        }

        if (!configuracaoCurCronograma.periodoValido(ctx.dataInicio.getText(), ctx.dataFinal.getText())) {
            erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": Periodo invalido");
        }
        return super.visitPeriodo(ctx);
    }
}