package br.ufscar.dc.compilador.semantico;

import br.ufscar.dc.antlr.ChronologicalBaseVisitor;
import br.ufscar.dc.antlr.ChronologicalParser.AtividadeContext;
import br.ufscar.dc.antlr.ChronologicalParser.CronogramaContext;
import br.ufscar.dc.antlr.ChronologicalParser.DependenciaContext;
import br.ufscar.dc.compilador.erros.ErroSemantico;
import br.ufscar.dc.compilador.semantico.tabela.ListaDeTabelas;
import br.ufscar.dc.compilador.semantico.tabela.Periodo;

public class AnalisadorDeDependencias extends ChronologicalBaseVisitor<Void> {

    public final ListaDeTabelas tabelas = ListaDeTabelas.getInstance();
    public final ErroSemantico erros = ErroSemantico.getInstance();

    private String cronogramaAtual;
    private String atividadeAtual;

    @Override
    public Void visitCronograma(CronogramaContext ctx) {
        cronogramaAtual = ctx.IDENT().getText();
        return super.visitCronograma(ctx);
    }

    @Override
    public Void visitAtividade(AtividadeContext ctx) {
        atividadeAtual = ctx.IDENT().getText();
        return super.visitAtividade(ctx);
    }

    @Override
    public Void visitDependencia(DependenciaContext ctx) {
        String nomeAtividadeDep = ctx.IDENT().getText();
        int numPeriodo = 1; // Periodo padrao para comparacao e o primeiro

        if (!tabelas.temSimbolo(cronogramaAtual, nomeAtividadeDep)) {
            erros.adicionarErro(
                    "Linha " + ctx.getStart().getLine() + ": Atividade " + ctx.IDENT().getText() + " nao declarada");
        } else {
            if (ctx.NUMERO_INTEIRO() == null) {
                // Atividade deve ter apenas um periodo
                if (tabelas.getPeriodosDeAtividade(cronogramaAtual, nomeAtividadeDep).size() > 1) {
                    erros.adicionarErro(
                            "Linha " + ctx.getStart().getLine() + ": Periodo de dependencia nao especificado");
                }
            } else {
                // Parsing do numero do periodo
                numPeriodo = Integer.parseInt(ctx.NUMERO_INTEIRO().getText());
            }

            try {
                // Verifica as datas
                Periodo perAtividadeAtual, perAtividadeDep;
                // Sempre compara com o primeiro periodo da atividade atual
                perAtividadeAtual = tabelas.getPeriodosDeAtividade(cronogramaAtual, atividadeAtual).get(0);
                perAtividadeDep = tabelas.getPeriodosDeAtividade(cronogramaAtual, nomeAtividadeDep).get(numPeriodo - 1);

                if (perAtividadeAtual.dataInicial.before(perAtividadeDep.dataFinal)) {
                    erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": Periodo " + atividadeAtual
                            + ".1 ocorre antes da dependencia " + ctx.getText());
                }
            } catch (IndexOutOfBoundsException ex) {
                // Atividade deve ter o periodo especificado
                erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": Periodo " + ctx.getText()
                        + " especificado nao existe.");
            }

        }
        return super.visitDependencia(ctx);
    }
}