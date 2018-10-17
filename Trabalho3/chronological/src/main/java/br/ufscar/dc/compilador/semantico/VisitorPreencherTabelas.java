package br.ufscar.dc.compilador.semantico;

import br.ufscar.dc.antlr.ChronologicalBaseVisitor;
import br.ufscar.dc.antlr.ChronologicalParser.AtividadeContext;
import br.ufscar.dc.antlr.ChronologicalParser.CronogramaContext;
import br.ufscar.dc.compilador.erros.ErroSemantico;
import br.ufscar.dc.compilador.semantico.tabela.ListaDeTabelas;

public class VisitorPreencherTabelas extends ChronologicalBaseVisitor<Void> {

    public final ListaDeTabelas tabelas = ListaDeTabelas.getInstance();
    public final ErroSemantico erros = ErroSemantico.getInstance();

    private String cronogramaAtual;

    @Override
    public Void visitCronograma(CronogramaContext ctx) {
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

    @Override
    public Void visitAtividade(AtividadeContext ctx) {
        if (!tabelas.temSimbolo(cronogramaAtual, ctx.IDENT().getText())) {
            tabelas.adicionarSimbolo(cronogramaAtual, ctx.IDENT().getText());
        } else {
            erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": atividade ja declarada");
        }
        return super.visitAtividade(ctx);
    }
}