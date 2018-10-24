package br.ufscar.dc.compilador.semantico;

import java.text.ParseException;
import java.util.Date;

import br.ufscar.dc.antlr.ChronologicalBaseVisitor;
import br.ufscar.dc.antlr.ChronologicalParser.*;
import br.ufscar.dc.compilador.erros.ErroSemantico;
import br.ufscar.dc.compilador.semantico.tabela.ListaDeTabelas;
import br.ufscar.dc.compilador.utils.Configuracoes;

public class AnalisadorSemantico extends ChronologicalBaseVisitor<Void> {

    public final ListaDeTabelas tabelas = ListaDeTabelas.getInstance();
    public final ErroSemantico erros = ErroSemantico.getInstance();

    private String cronogramaAtual;
    private Configuracoes configuracaoCurCronograma;

    // cronograma: 'Cronograma' IDENT '{' (atividades)* (configuracao)? '}';
    @Override
    public Void visitCronograma(CronogramaContext ctx) {
        configuracaoCurCronograma = new Configuracoes(ctx.configuracao());

        if (!tabelas.temTabela(ctx.IDENT().getText())) {
            tabelas.criarTabela(ctx.IDENT().getText());
            cronogramaAtual = ctx.IDENT().getText();
            return super.visitCronograma(ctx);
        } else {
            // Se o cronograma ja foi declarado antes, impede de visita-lo
            erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": cronograma já declarado");
            return null;
        }
    }

    // atividade: 'Atividade' IDENT '{' (descricao ',' datas (',' configuracao)?
    // (',' dependencias)?) '}';
    @Override
    public Void visitAtividade(AtividadeContext ctx) {
        if (!tabelas.temSimbolo(cronogramaAtual, ctx.IDENT().getText())) {
            tabelas.adicionarSimbolo(cronogramaAtual, ctx.IDENT().getText());
        } else {
            erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": atividade ja declarada");
        }
        return super.visitAtividade(ctx);
    }

    // periodo: dataInicio = DATA '-' dataFinal = DATA;
    @Override
    public Void visitPeriodo(PeriodoContext ctx) {
        Date dataInicial, dataFinal;
        boolean periodoValido = true;

        // Verifica datas de acordo com o padrao das configuracoes
        try {
            dataInicial = configuracaoCurCronograma.dataFormater.parse(ctx.dataInicio.getText());
            dataFinal = configuracaoCurCronograma.dataFormater.parse(ctx.dataFinal.getText());
        } catch (ParseException e) {
            erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": Data  nao esta conforme o padrao "
                    + configuracaoCurCronograma.formatoData);
            return super.visitPeriodo(ctx);
        }

        // Verifica se o periodo e valido
        if (dataInicial.after(dataFinal)) {
            erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": Periodo invalido");
            periodoValido = false;
        }

        // Adiciona datas a atividade atual
        if (periodoValido) {
            tabelas.adicionarPeriodo(cronogramaAtual, dataInicial, dataFinal);
        }

        return super.visitPeriodo(ctx);
    }

    // data_formato: LITTLE_ENDIAN | MID_ENDIAN | BIG_ENDIAN;
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
}