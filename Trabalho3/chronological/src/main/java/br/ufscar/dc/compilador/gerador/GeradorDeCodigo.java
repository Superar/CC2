package br.ufscar.dc.compilador.gerador;

import br.ufscar.dc.antlr.ChronologicalBaseListener;
import br.ufscar.dc.antlr.ChronologicalParser.AtividadeContext;
import br.ufscar.dc.antlr.ChronologicalParser.CronogramaContext;
import br.ufscar.dc.antlr.ChronologicalParser.CronogramasContext;

public class GeradorDeCodigo extends ChronologicalBaseListener {
    private String CodigoGerado;
    private String CodigoTabela;
    private String CodigoGrafico;

    // Configuracoes do cronograma atual
    private Configuracoes configuracaoCurCronograma;

    // Numero da atividade atual dentro do cronograma
    private int curAtividade;

    public String getCodigo() {
        return CodigoGerado;
    }

    public GeradorDeCodigo() {
        super();
        CodigoGerado = "";
    }

    @Override
    public void enterCronogramas(CronogramasContext ctx) {
        // Preamble do documento LaTeX
        CodigoGerado += "\\documentclass[a4paper,10pt]{article}\n";
        CodigoGerado += "\\usepackage{longtable}\n";
        CodigoGerado += "\\newcommand{\\myrowcolour}{\\rowcolor[gray]{0.925}}\n";
        CodigoGerado += "\\usepackage{booktabs}\n";
        CodigoGerado += "\\usepackage{pgfgantt}\n";
        CodigoGerado += "\\usepackage{pdflandscape}\n";
        CodigoGerado += "\\usepackage[utf8]{inputenc}\n";
        CodigoGerado += "\\usepackage[margin=.25in]{geometry}\n";
        CodigoGerado += "\\pagenumbering{gobble}\n";
        CodigoGerado += "\\begin{document}\n";
    }

    @Override
    public void exitCronogramas(CronogramasContext ctx) {
        CodigoGerado += "\\end{document}";
    }

    @Override
    public void enterCronograma(CronogramaContext ctx) {
        // Reinicia as strings com o codigo da tabela e do grafico alem do numero da
        // atividade atual
        CodigoTabela = "";
        CodigoGrafico = "";
        curAtividade = 0;

        // Analisa as configuracoes a serem utilizadas no cronograma
        configuracaoCurCronograma = new Configuracoes(ctx.configuracao());

        CodigoTabela += "\\begin{longtable}{p{0.97\\textwidth}}\n";
        CodigoTabela += "\\toprule\n";

        CodigoGrafico += "\\begin{landscape}\n";
        CodigoGrafico += "\\begin{center}\n";
    }

    @Override
    public void exitCronograma(CronogramaContext ctx) {
        CodigoTabela += "\\bottomrule\n";
        CodigoTabela += "\\end{longtable}\n";
        CodigoTabela += "\\newpage\n";

        CodigoGrafico += "\\end{ganttchart}";
        CodigoGrafico += "\\end{center}";
        CodigoGrafico += "\\end{landscape}";

        CodigoGerado += CodigoTabela;
        CodigoGerado += CodigoGrafico;
    }

    @Override
    public void enterAtividade(AtividadeContext ctx) {
        curAtividade++;

        // Configuracoes locais da atividade
        Configuracoes configuracoesAtividade = new Configuracoes(ctx.configuracao());

        // A primeira atividade nao possui midrule, possui toprule
        // (adicionada em enterCronograma)
        if (curAtividade != 1) {
            CodigoTabela += "\\midrule\n";
        }
        CodigoTabela += "\\myrowcolour\n";
        // Cabecalho
        CodigoTabela += "\\bfseries Atividade " + curAtividade + ": " + ctx.IDENT().getText() + " \\\\\n";
        CodigoTabela += "\\midrule\n";
        // Descricao
        CodigoTabela += ctx.descricao().TEXTO().getText();
        CodigoTabela += "\n\\\\\n";
    }
}