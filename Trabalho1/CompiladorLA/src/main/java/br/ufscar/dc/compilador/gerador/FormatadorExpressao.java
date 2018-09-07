package br.ufscar.dc.compilador.gerador;

import br.ufscar.dc.antlr.LABaseVisitor;
import br.ufscar.dc.antlr.LAParser.Exp_aritmeticaContext;
import br.ufscar.dc.antlr.LAParser.ExpressaoContext;
import br.ufscar.dc.antlr.LAParser.Fator_logicoContext;
import br.ufscar.dc.antlr.LAParser.Op1Context;
import br.ufscar.dc.antlr.LAParser.Op2Context;
import br.ufscar.dc.antlr.LAParser.Op3Context;
import br.ufscar.dc.antlr.LAParser.Op_logico_1Context;
import br.ufscar.dc.antlr.LAParser.Op_logico_2Context;
import br.ufscar.dc.antlr.LAParser.Op_relacionalContext;
import br.ufscar.dc.antlr.LAParser.Parcela_nao_unarioContext;
import br.ufscar.dc.antlr.LAParser.Parcela_unarioContext;

/**
 * Visitor que recebe um ExpressaoContext e monta uma string subtitituindo
 * tokens especificos da linguagem LA por tokens da linguagem C
 */
public class FormatadorExpressao extends LABaseVisitor<String> {
    public FormatadorExpressao() {
        super();
    }

    public String formataExpressao(ExpressaoContext ctx) {
        return visitChildren(ctx);
    }

    public String formataExpressaoAritmetica(Exp_aritmeticaContext ctx) {
        return visitChildren(ctx);
    }

    // ------- OPERADORES -------
    @Override
    public String visitOp_relacional(Op_relacionalContext ctx) {
        visitChildren(ctx);
        if (ctx.getText().equals("=")) {
            return "==";
        } else if (ctx.getText().equals("<>")) {
            return "!=";
        } else {
            return ctx.getText();
        }
    }

    @Override
    public String visitOp1(Op1Context ctx) {
        visitChildren(ctx);
        return ctx.getText();
    }

    @Override
    public String visitOp2(Op2Context ctx) {
        visitChildren(ctx);
        return ctx.getText();
    }

    @Override
    public String visitOp3(Op3Context ctx) {
        visitChildren(ctx);
        return ctx.getText();
    }

    @Override
    public String visitOp_logico_1(Op_logico_1Context ctx) {
        visitChildren(ctx);
        return "||";
    }

    @Override
    public String visitOp_logico_2(Op_logico_2Context ctx) {
        visitChildren(ctx);
        return "&&";
    }

    @Override
    public String visitFator_logico(Fator_logicoContext ctx) {
        String ret = visitChildren(ctx);
        if (ctx.nao != null) {
            return "!(" + ret + ")";
        } else {
            return ret;
        }
    }

    // ------- OUTROS TOKENS -------
    @Override
    public String visitParcela_unario(Parcela_unarioContext ctx) {
        String ret = visitChildren(ctx);

        if (ctx.NUM_INT() != null || ctx.NUM_REAL() != null || ctx.identificador() != null) {
            return ctx.getText();
        } else {
            return ret;
        }
    }

    @Override
    public String visitParcela_nao_unario(Parcela_nao_unarioContext ctx) {
        visitChildren(ctx);
        return ctx.getText();
    }

    // Metodo VisitChildren retornara uma concatenacao do retorno de todos os
    // filhos
    @Override
    protected String aggregateResult(String aggregate, String nextResult) {
        if (nextResult != null) {
            if (aggregate == null) {
                return nextResult;
            } else {
                return aggregate + nextResult;
            }
        } else {
            return aggregate;
        }
    }
}