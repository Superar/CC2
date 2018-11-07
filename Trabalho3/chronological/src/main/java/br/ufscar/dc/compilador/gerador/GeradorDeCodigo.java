package br.ufscar.dc.compilador.gerador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import br.ufscar.dc.antlr.ChronologicalBaseListener;
import br.ufscar.dc.antlr.ChronologicalParser.AtividadeContext;
import br.ufscar.dc.antlr.ChronologicalParser.AtividadesContext;
import br.ufscar.dc.antlr.ChronologicalParser.CronogramaContext;
import br.ufscar.dc.antlr.ChronologicalParser.CronogramasContext;
import br.ufscar.dc.antlr.ChronologicalParser.DependenciaContext;
import br.ufscar.dc.compilador.semantico.tabela.ListaDeTabelas;
import br.ufscar.dc.compilador.semantico.tabela.Periodo;
import br.ufscar.dc.compilador.utils.*;

public class GeradorDeCodigo extends ChronologicalBaseListener {
    private String codigoGerado;
    private String codigoTabela;
    private String codigoGrafico;
    private String codigoDependencias;

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
        return codigoGerado;
    }

    public GeradorDeCodigo() {
        super();
        codigoGerado = "";
    }

    @Override
    public void enterCronogramas(CronogramasContext ctx) {
        // Preamble do documento LaTeX
        codigoGerado += "\\documentclass[a4paper,10pt]{article}\n";
        codigoGerado += "\\usepackage{longtable}\n";
        codigoGerado += "\\usepackage{colortbl}\n";
        codigoGerado += "\\newcommand{\\myrowcolour}{\\rowcolor[gray]{0.925}}\n";
        codigoGerado += "\\usepackage{booktabs}\n";
        codigoGerado += "\\usepackage{pgfgantt}\n";
        codigoGerado += "\\usepackage{pdflscape}\n";
        codigoGerado += "\\usepackage[utf8]{inputenc}\n";
        codigoGerado += "\\usepackage[margin=.25in]{geometry}\n";
        codigoGerado += "\\pagenumbering{gobble}\n";
        codigoGerado += "\\begin{document}\n";
    }

    @Override
    public void exitCronogramas(CronogramasContext ctx) {
        codigoGerado += "\\end{document}";
    }

    @Override
    public void enterCronograma(CronogramaContext ctx) {
        // Reinicia as strings com o codigo da tabela e do grafico alem do numero da
        // atividade atual
        codigoTabela = "";
        codigoGrafico = "";
        codigoDependencias = "";
        curAtividade = 0;
        curCronograma = ctx.IDENT().getText();

        // Analisa as configuracoes a serem utilizadas no cronograma
        if (ctx.configuracao() != null) {
            configuracaoCurCronograma = new Configuracoes(ctx.configuracao());
        } else {
            // Configuracao padrao
            configuracaoCurCronograma = new Configuracoes();
        }

        // Definicoes de cores
        codigoGerado += "\\definecolor{" + ctx.IDENT().getText() + "}{" + configuracaoCurCronograma.esquemaDeCores
                + "}{" + configuracaoCurCronograma.cor + "}\n";

        /*----------- TABELA -----------*/
        codigoTabela += "\\begin{longtable}{p{0.97\\textwidth}}\n";
        codigoTabela += "\\toprule\n";

        /*----------- GRAFICO -----------*/
        codigoGrafico += "\\begin{landscape}\n";
        codigoGrafico += "\\begin{center}\n";
        codigoGrafico += "\\begin{ganttchart}[";
        // Configuracoes do grafico
        if (configuracaoCurCronograma.linhasHorizontais) {
            codigoGrafico += "hgrid,";
        }
        if (configuracaoCurCronograma.linhasVerticais) {
            codigoGrafico += "vgrid,";
        }
        if (!configuracaoCurCronograma.mostrarDias) {
            codigoGrafico += "compress calendar,";
        }
        codigoGrafico += "time slot format=little-endian,";
        codigoGrafico += "bar/.append style={fill=" + ctx.IDENT().getText() + ", inner sep=0pt},";
        codigoGrafico += "milestone/.append style={fill=" + ctx.IDENT().getText() + ", inner sep=0pt},";
        codigoGrafico += "bar height=" + configuracaoCurCronograma.alturaBarra;
        codigoGrafico += "]{";
        // Datas limites do grafico
        codigoGrafico += pdfDateFormat.format(tabelas.dataInicialDeCronograma(ctx.IDENT().getText()));
        codigoGrafico += "}{";
        codigoGrafico += pdfDateFormat.format(tabelas.dataFinalDeCronograma(ctx.IDENT().getText()));
        codigoGrafico += "}\n";

        codigoGrafico += "\\gantttitlecalendar{year, month";
        if (configuracaoCurCronograma.mostrarDias) {
            codigoGrafico += ", day}\\\\\n";
        } else {
            codigoGrafico += "}\\\\\n";
        }
    }

    @Override
    public void exitCronograma(CronogramaContext ctx) {
        /*----------- TABELA -----------*/
        codigoTabela += "\\bottomrule\n";
        codigoTabela += "\\end{longtable}\n";
        codigoTabela += "\\newpage\n";

        /*----------- GRAFICO -----------*/
        codigoGrafico += "\\end{ganttchart}\n";
        codigoGrafico += "\\end{center}\n";
        codigoGrafico += "\\end{landscape}\n";

        /*----------- CODIGO FINAL -----------*/
        codigoGerado += codigoTabela;
        codigoGerado += codigoGrafico;
    }

    @Override
    public void exitAtividades(AtividadesContext ctx) {
        codigoGrafico += codigoDependencias;
        super.exitAtividades(ctx);
    }

    @Override
    public void enterAtividade(AtividadeContext ctx) {
        curAtividade++;

        Configuracoes configuracoesAtividade = null;
        // Configuracoes locais da atividade
        if (ctx.configuracao() != null) {
            configuracoesAtividade = new Configuracoes(ctx.configuracao());

            // Definicoes de cores
            if (configuracoesAtividade.corAlterada) {
                codigoGerado += "\\definecolor{" + curCronograma + "-" + ctx.IDENT().getText() + "}{"
                        + configuracoesAtividade.esquemaDeCores + "}{" + configuracoesAtividade.cor + "}\n";
            }
        }

        ArrayList<Periodo> periodos = tabelas.getPeriodosDeAtividade(curCronograma, ctx.IDENT().getText());

        /*----------- TABELA -----------*/
        // A primeira atividade nao possui midrule, possui toprule
        // (adicionada em enterCronograma)
        if (curAtividade != 1) {
            codigoTabela += "\\midrule\n";
        }
        codigoTabela += "\\myrowcolour\n";
        // Cabecalho
        codigoTabela += "\\bfseries Atividade " + curAtividade + ": " + ctx.IDENT().getText() + " \\\\\n";
        codigoTabela += "\\midrule\n";
        // Descricao
        // Deve-se ignorar as aspas que delimitam o texto da descricao
        codigoTabela += ctx.descricao().TEXTO().getText().substring(1, ctx.descricao().TEXTO().getText().length() - 1);
        codigoTabela += "\n\\\\\n\\\\\n";
        codigoTabela += "Datas de execu\\c{c}\\~{a}o:\\\\\n";

        // Datas
        for (Periodo p : periodos) {
            if (!p.dataInicial.equals(p.dataFinal)) {
                if (configuracoesAtividade != null) {
                    codigoTabela += configuracoesAtividade.dataFormater.format(p.dataInicial);
                    codigoTabela += " - ";
                    codigoTabela += configuracoesAtividade.dataFormater.format(p.dataFinal);
                } else {
                    codigoTabela += configuracaoCurCronograma.dataFormater.format(p.dataInicial);
                    codigoTabela += " - ";
                    codigoTabela += configuracaoCurCronograma.dataFormater.format(p.dataFinal);
                }
            } else {
                if (configuracoesAtividade != null) {
                    codigoTabela += configuracoesAtividade.dataFormater.format(p.dataInicial);
                } else {
                    codigoTabela += configuracaoCurCronograma.dataFormater.format(p.dataInicial);
                }
            }
            codigoTabela += "\\\\\n";
        }

        /*----------- GRAFICO -----------*/
        for (int num_periodo = 0; num_periodo < periodos.size(); num_periodo++) {
            Periodo p = periodos.get(num_periodo);
            if (!p.dataInicial.equals(p.dataFinal)) {
                codigoGrafico += "\\ganttbar[name=" + ctx.IDENT().getText() + "." + (num_periodo + 1);

                if (configuracoesAtividade != null) {
                    codigoGrafico += ", ";
                    if (configuracoesAtividade.corAlterada) {
                        codigoGrafico += "bar/.append style={fill=" + curCronograma + "-" + ctx.IDENT().getText()
                                + ", inner sep=0pt}";
                        if (configuracoesAtividade.alturaBarraAlterada) {
                            codigoGrafico += ", ";
                        }
                    }
                    if (configuracoesAtividade.alturaBarraAlterada) {
                        codigoGrafico += "bar height=" + configuracoesAtividade.alturaBarra;
                    }
                }
                codigoGrafico += "]";

                if (num_periodo == 0) {
                    codigoGrafico += "{Atividade " + curAtividade + "}";
                } else {
                    codigoGrafico += "{}";
                }
                codigoGrafico += "{" + pdfDateFormat.format(p.dataInicial) + "}{";
                codigoGrafico += pdfDateFormat.format(p.dataFinal) + "}";
            } else {
                codigoGrafico += "\\ganttmilestone[name=" + ctx.IDENT().getText() + "." + (num_periodo + 1);

                if (configuracoesAtividade != null) {
                    codigoGrafico += ", ";
                    if (configuracoesAtividade.corAlterada) {
                        codigoGrafico += "milestone/.append style={fill=" + curCronograma + "-" + ctx.IDENT().getText()
                                + ", inner sep=0pt}";
                        if (configuracoesAtividade.alturaBarraAlterada) {
                            codigoGrafico += ", ";
                        }
                    }
                    if (configuracoesAtividade.alturaBarraAlterada) {
                        codigoGrafico += "milestone height=" + configuracoesAtividade.alturaBarra;
                    }
                }
                codigoGrafico += "]";

                if (num_periodo == 0) {
                    codigoGrafico += "{Atividade " + curAtividade + "}";
                } else {
                    codigoGrafico += "{}";
                }
                codigoGrafico += "{" + pdfDateFormat.format(p.dataInicial) + "}";
            }

            if (num_periodo == periodos.size() - 1) {
                codigoGrafico += "\\\\\n";
            } else {
                codigoGrafico += "\n";
            }
        }
    }

    @Override
    public void enterDependencia(DependenciaContext ctx) {
        codigoDependencias += "\\ganttlink{" + ctx.IDENT().getText() + ".";
        if (ctx.NUMERO_INTEIRO() == null) {
            codigoDependencias += "1";
        } else {
            codigoDependencias += ctx.NUMERO_INTEIRO().getText();
        }
        codigoDependencias += "}{";

        // Percorre a arvore em direcao a raiz para recuperar o nome da atividade
        // em que a dependencia sera conectada
        AtividadeContext atividade = (AtividadeContext) ctx.getParent().getParent();
        codigoDependencias += atividade.IDENT().getText();
        codigoDependencias += ".1}\n";

        super.enterDependencia(ctx);
    }
}