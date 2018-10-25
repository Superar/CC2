package br.ufscar.dc.compilador.gerador;

import java.text.SimpleDateFormat;

import br.ufscar.dc.antlr.ChronologicalBaseListener;
import br.ufscar.dc.antlr.ChronologicalParser.AtividadeContext;
import br.ufscar.dc.antlr.ChronologicalParser.CronogramaContext;
import br.ufscar.dc.antlr.ChronologicalParser.CronogramasContext;
import br.ufscar.dc.compilador.semantico.tabela.ListaDeTabelas;
import br.ufscar.dc.compilador.semantico.tabela.Periodo;
import br.ufscar.dc.compilador.utils.*;

public class GeradorDeCodigo extends ChronologicalBaseListener {
    private String CodigoGerado;
    private String CodigoTabela;
    private String CodigoGrafico;

    // Tabela de simbolos
    private static final ListaDeTabelas tabelas = ListaDeTabelas.getInstance();

    // Configuracoes do cronograma atual
    private Configuracoes configuracaoCurCronograma;

    // Nome do crogonrama atual sendo processado
    private String curCronograma;
    // Numero da atividade atual dentro do cronograma
    private int curAtividade;

    // Formatador de datas no formato little-endian
    private final static SimpleDateFormat pdfDateFormat = new SimpleDateFormat("dd/MM/yyyy");

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
        CodigoGerado += "\\usepackage{colortbl}\n";
        CodigoGerado += "\\newcommand{\\myrowcolour}{\\rowcolor[gray]{0.925}}\n";
        CodigoGerado += "\\usepackage{booktabs}\n";
        CodigoGerado += "\\usepackage{pgfgantt}\n";
        CodigoGerado += "\\usepackage{pdflscape}\n";
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
        curCronograma = ctx.IDENT().getText();

        // Analisa as configuracoes a serem utilizadas no cronograma
        configuracaoCurCronograma = new Configuracoes(ctx.configuracao());

        // Definicoes de cores
        CodigoGerado += "\\definecolor{" + ctx.IDENT().getText() + "}{" + configuracaoCurCronograma.esquemaDeCores
                + "}{" + configuracaoCurCronograma.cor + "}\n";

        /*----------- TABELA -----------*/
        CodigoTabela += "\\begin{longtable}{p{0.97\\textwidth}}\n";
        CodigoTabela += "\\toprule\n";

        /*----------- GRAFICO -----------*/
        CodigoGrafico += "\\begin{landscape}\n";
        CodigoGrafico += "\\begin{center}\n";
        CodigoGrafico += "\\begin{ganttchart}[";
        // Configuracoes do grafico
        if (configuracaoCurCronograma.linhasHorizontais) {
            CodigoGrafico += "hgrid,";
        }
        if (configuracaoCurCronograma.linhasVerticais) {
            CodigoGrafico += "vgrid,";
        }
        CodigoGrafico += "time slot format=little-endian,";
        CodigoGrafico += "inline,";
        CodigoGrafico += "bar/.append style={fill=" + ctx.IDENT().getText() + ", inner sep=0pt},";
        CodigoGrafico += "bar height=" + configuracaoCurCronograma.alturaBarra;
        CodigoGrafico += "]{";
        // Datas limites do grafico
        CodigoGrafico += pdfDateFormat.format(tabelas.dataInicialDeCronograma(ctx.IDENT().getText()));
        CodigoGrafico += "}{";
        CodigoGrafico += pdfDateFormat.format(tabelas.dataFinalDeCronograma(ctx.IDENT().getText()));
        CodigoGrafico += "}\n";

        CodigoGrafico += "\\gantttitlecalendar{year, month";
        if (configuracaoCurCronograma.mostrarDias) {
            CodigoGrafico += ", day}\\\\\n";
        } else {
            CodigoGrafico += "}\\\\\n";
        }
    }

    @Override
    public void exitCronograma(CronogramaContext ctx) {
        /*----------- TABELA -----------*/
        CodigoTabela += "\\bottomrule\n";
        CodigoTabela += "\\end{longtable}\n";
        CodigoTabela += "\\newpage\n";

        /*----------- GRAFICO -----------*/
        CodigoGrafico += "\\end{ganttchart}\n";
        CodigoGrafico += "\\end{center}\n";
        CodigoGrafico += "\\end{landscape}\n";

        /*----------- CODIGO FINAL -----------*/
        CodigoGerado += CodigoTabela;
        CodigoGerado += CodigoGrafico;
    }

    @Override
    public void enterAtividade(AtividadeContext ctx) {
        curAtividade++;

        // Configuracoes locais da atividade
        if (ctx.configuracao() != null) {
            Configuracoes configuracoesAtividade = new Configuracoes(ctx.configuracao());
        }

        /*----------- TABELA -----------*/
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
        // Deve-se ignorar as aspas que delimitam o texto da descricao
        CodigoTabela += ctx.descricao().TEXTO().getText().substring(1, ctx.descricao().TEXTO().getText().length() - 1);
        CodigoTabela += "\n\\\\\n";

        /*----------- GRAFICO -----------*/
        for (Periodo p : tabelas.getPeriodosDeAtividade(curCronograma, ctx.IDENT().getText())) {
            if (!p.dataInicial.equals(p.dataFinal)) {
                CodigoGrafico += "\\ganttbar{" + ctx.IDENT().getText() + "}{";
                CodigoGrafico += pdfDateFormat.format(p.dataInicial) + "}{";
                CodigoGrafico += pdfDateFormat.format(p.dataFinal) + "}\\\\\n";
            } else {
                CodigoGrafico += "\\ganttmilestone{" + ctx.IDENT().getText() + "}{";
                CodigoGrafico += pdfDateFormat.format(p.dataInicial) + "}\\\\\n";
            }
        }
    }
}