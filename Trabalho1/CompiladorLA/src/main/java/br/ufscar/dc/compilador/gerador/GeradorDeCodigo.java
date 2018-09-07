package br.ufscar.dc.compilador.gerador;

import java.util.ArrayList;
import java.util.Arrays;

import br.ufscar.dc.antlr.LABaseListener;
import br.ufscar.dc.antlr.LAParser.*;

public class GeradorDeCodigo extends LABaseListener {

    String codigo;
    FormatadorExpressao expFmt;

    public GeradorDeCodigo() {
        super();
        codigo = "";
        expFmt = new FormatadorExpressao();
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public void enterPrograma(ProgramaContext ctx) {
        codigo += "#include <stdlib.h>\n";
        codigo += "#include <stdio.h>\n";
    }

    @Override
    public void enterCorpo(CorpoContext ctx) {
        codigo += "int main () {";
    }

    @Override
    public void exitCorpo(CorpoContext ctx) {
        codigo += "return 0;";
        codigo += "}";
    }

    @Override
    public void enterVariavel(VariavelContext ctx) {
        if (ctx.tipo().getText().equals("inteiro")) {
            codigo += "int ";
        } else if (ctx.tipo().getText().equals("real")) {
            codigo += "float ";
        } else if (ctx.tipo().getText().equals("literal")) {
            codigo += "char ";
        }

        codigo += ctx.primeiroIdentificador.getText();
        if (ctx.tipo().getText().equals("literal")) {
            codigo += "[80]";
        }

        if (ctx.listaIdentificador != null) {
            for (IdentificadorContext identificador : ctx.listaIdentificador) {
                codigo += ", ";
                codigo += identificador.getText();
                if (ctx.tipo().getText().equals("literal")) {
                    codigo += "[80]";
                }
            }
        }

        codigo += ";";
    }

    @Override
    public void enterCmdLeia(CmdLeiaContext ctx) {
        if (ctx.primeiroIdentificador.tipoVar.equals("literal")) {
            codigo += "gets(";
        } else {
            codigo += "scanf(\"";
            if (ctx.primeiroIdentificador.tipoVar.equals("inteiro")) {
                codigo += "%d";
            } else if (ctx.primeiroIdentificador.tipoVar.equals("real")) {
                codigo += "%f";
            }
            codigo += "\",&";
        }
        codigo += ctx.primeiroIdentificador.getText();
        codigo += ");";
    }

    @Override
    public void enterCmdEscreva(CmdEscrevaContext ctx) {
        codigo += formatoCmdEscreva(ctx.primeiraExpressao);

        if (ctx.listaExpressao.size() > 0) {
            for (ExpressaoContext expressao : ctx.listaExpressao) {
                codigo += formatoCmdEscreva(expressao);
            }
        }
    }

    // Formata o comando de printf para uma determinada expressao
    public String formatoCmdEscreva(ExpressaoContext ctx) {
        // Pega apenas o primeiro tipo em tipoVar
        // Como nao ocorreu erro semantico, todos os tipos serao iguais
        String tipo = new ArrayList<>(Arrays.asList(ctx.tipoVar.split(","))).get(0);

        String comando = "";
        comando += "printf(\"";
        if (tipo.equals("inteiro")) {
            comando += "%d";
        } else if (tipo.equals("real")) {
            comando += "%f";
        } else if (tipo.equals("literal")) {
            comando += "%s";
        }
        comando += "\",";
        comando += ctx.getText();
        comando += ");";
        return comando;
    }

    @Override
    public void enterCmdAtribuicao(CmdAtribuicaoContext ctx) {
        codigo += ctx.identificador().getText();
        codigo += "=";
        codigo += expFmt.formataExpressao(ctx.expressao());
        codigo += ";";
    }

    @Override
    public void enterCmdSe(CmdSeContext ctx) {
        codigo += "if(";
        codigo += expFmt.formataExpressao(ctx.expressao());
        codigo += "){";
    }

    @Override
    public void exitCmdSe(CmdSeContext ctx) {
        codigo += "}";
    }

    @Override
    public void enterCmdSenao(CmdSenaoContext ctx) {
        codigo += "}else{";
    }

    @Override
    public void enterCmdCaso(CmdCasoContext ctx) {
        codigo += "switch(";
        codigo += expFmt.formataExpressaoAritmetica(ctx.exp_aritmetica());
        codigo += "){";
    }

    @Override
    public void exitCmdCaso(CmdCasoContext ctx) {
        codigo += "}";
    }

    @Override
    public void enterItem_selecao(Item_selecaoContext ctx) {
        // Realiza o split do valor em "contantes" com respeito a ..
        // Se for um intervalo, havera dois valores
        // Se for uma constante apenas sera um valor
        ArrayList<String> intervalo = new ArrayList<>(Arrays.asList(ctx.constantes().getText().split("\\.\\.")));

        if (intervalo.size() > 1) { // Intervalo
            int inicio = Integer.valueOf(intervalo.get(0));
            int fim = Integer.valueOf(intervalo.get(1));
            for (int i = inicio; i < fim + 1; i++) {
                codigo += "case ";
                codigo += i;
                codigo += ":";
            }
        } else { // Apenas uma constante
            codigo += "case ";
            codigo += Integer.valueOf(intervalo.get(0));
            codigo += ":";
        }
    }

    @Override
    public void exitItem_selecao(Item_selecaoContext ctx) {
        codigo += "break;";
    }

    @Override
    public void enterCmdCasoSenao(CmdCasoSenaoContext ctx) {
        codigo += "default:";
    }

    @Override
    public void enterDecl_local_global(Decl_local_globalContext ctx) {
        if (ctx.declaracao_local() != null) {
            if (ctx.declaracao_local().valor_constante() != null) { // Declaracao de constante
                codigo += "#define ";
                codigo += ctx.declaracao_local().IDENT().getText();
                codigo += " ";
                codigo += ctx.declaracao_local().valor_constante().getText();
                codigo += "\n";
            }
        }
    }

    @Override
    public void enterCmdPara(CmdParaContext ctx) {
        codigo += "for(";
        codigo += ctx.IDENT().getText();
        codigo += "=";
        codigo += expFmt.formataExpressaoAritmetica(ctx.exp_aritmetica(0));
        codigo += ";";
        codigo += ctx.IDENT().getText();
        codigo += "<=";
        codigo += expFmt.formataExpressaoAritmetica(ctx.exp_aritmetica(1));
        codigo += ";";
        codigo += ctx.IDENT().getText();
        codigo += "++){";
    }

    @Override
    public void exitCmdPara(CmdParaContext ctx) {
        codigo += "}";
    }

    @Override
    public void enterCmdEnquanto(CmdEnquantoContext ctx) {
        codigo += "while(";
        codigo += expFmt.formataExpressao(ctx.expressao());
        codigo += "){";
    }

    @Override
    public void exitCmdEnquanto(CmdEnquantoContext ctx) {
        codigo += "}";
    }

    @Override
    public void enterCmdFaca(CmdFacaContext ctx) {
        codigo += "do{";
    }

    @Override
    public void exitCmdFaca(CmdFacaContext ctx) {
        codigo += "}while(";
        codigo += expFmt.formataExpressao(ctx.expressao());
        codigo += ");";
    }

    @Override
    public void enterCmdChamada(CmdChamadaContext ctx) {
        codigo += ctx.IDENT();
        codigo += "(";
        codigo += expFmt.formataExpressao(ctx.primeiraExp);
        if (ctx.listaExp != null) {
            for (ExpressaoContext expressao : ctx.listaExp) {
                codigo += ",";
                codigo += expFmt.formataExpressao(expressao);
            }
        }
        codigo += ");";
    }

    @Override
    public void enterCmdRetorne(CmdRetorneContext ctx) {
        codigo += "return ";
        codigo += expFmt.formataExpressao(ctx.expressao());
        codigo += ";";
    }

    @Override
    public void enterDeclaracao_global(Declaracao_globalContext ctx) {
        if (ctx.procedimento != null) {
            codigo += "void ";
        } else {
            if (ctx.tipo_estentido().getText().equals("inteiro")) {
                codigo += "int ";
            }
        }
        codigo += ctx.IDENT().getText();
        codigo += "(";
    }

    @Override
    public void exitDeclaracao_global(Declaracao_globalContext ctx) {
        codigo += "}";
    }

    @Override
    public void enterParametros(ParametrosContext ctx) {
        for (ParametroContext parametro : ctx.parametro()) {
            for (IdentificadorContext identificador : parametro.identificador()) {
                if (identificador.tipoVar.equals("literal")) {
                    codigo += "char* ";
                } else if (identificador.tipoVar.equals("inteiro")) {
                    codigo += "int ";
                }
                codigo += identificador.getText();
            }
        }
    }

    @Override
    public void exitParametros(ParametrosContext ctx) {
        codigo += "){";
    }
}