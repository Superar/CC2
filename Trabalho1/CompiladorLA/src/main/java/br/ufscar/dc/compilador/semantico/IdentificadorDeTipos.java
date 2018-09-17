package br.ufscar.dc.compilador.semantico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.antlr.v4.runtime.Token;

import br.ufscar.dc.antlr.LABaseVisitor;
import br.ufscar.dc.antlr.LAParser.*;

public class IdentificadorDeTipos extends LABaseVisitor<String> {
    PilhaDeTabelas escopos;

    public IdentificadorDeTipos(PilhaDeTabelas escopos) {
        super();
        this.escopos = escopos;
    }

    public String identificaTipoExpressao(ExpressaoContext ctx) {
        String ret = visitExpressao(ctx);
        if (ret == null) {
            return null;
        }

        ArrayList<String> tipos = new ArrayList<>(Arrays.asList(ret.split(",")));

        // Substitui valores
        // Ponteiros devem receber enderecos
        Collections.replaceAll(tipos, "^inteiro", "ponteiro");

        if (tipos.stream().distinct().limit(2).count() > 1) {
            List<String> listaTiposDistintos = tipos.stream().distinct().limit(2).sorted().collect(Collectors.toList());
            // Mais de um tipo
            if (listaTiposDistintos.equals(new ArrayList<>(Arrays.asList("inteiro", "real")))) {
                // Os dois valores sao inteiro e real
                // A expressao e real
                return "real";
            }
            return null;
        } else {
            ctx.tipoVar = ret.split(",")[0];
            return tipos.get(0);
        }
    }

    // Funcao que muda o comportamento dos retornos do metodo visitChildren
    // O metodo visitChildren retornara uma string com todos os retornos dos
    // filhos separados por virgula
    @Override
    protected String aggregateResult(String aggregate, String nextResult) {
        if (nextResult != null) {
            if (aggregate == null) {
                return nextResult;
            } else {
                return aggregate + "," + nextResult;
            }
        } else {
            return aggregate;
        }
    }

    /*
     * parcela_unario: ('^')? identificador | IDENT '(' expressao (',' expressao)*
     * ')' | NUM_INT | NUM_REAL | '(' expressao ')';
     */
    @Override
    public String visitParcela_unario(Parcela_unarioContext ctx) {
        String ret = visitChildren(ctx);

        // Verifica o tipo da parcela
        // Caso nao seja inteiro, ou real, sera o tipo do identificador
        // Caso nao seja nenhum desses tipos, sera o tipo da expressao entre
        // parenteses
        if (ctx.NUM_INT() != null) {
            return "inteiro";
        } else if (ctx.NUM_REAL() != null) {
            return "real";
        } else if (ctx.identificador() != null) {
            String ident = ctx.identificador().primeiroIdent.getText();
            for (Token tok : ctx.identificador().listaIdent) {
                ident += ".";
                ident += tok.getText();
            }
            return escopos.getTipoPorNome(ident);
        } else if (ctx.IDENT() != null) {
            return escopos.getTipoPorNome(ctx.IDENT().getText());
        } else {
            return ret;
        }
    }

    // parcela_nao_unario: '&' identificador | CADEIA;
    @Override
    public String visitParcela_nao_unario(Parcela_nao_unarioContext ctx) {
        visitChildren(ctx);

        // Se for cadeira, retorna o tipo literal, senao sera ponteiro
        if (ctx.CADEIA() != null) {
            return "literal";
        } else {
            return "ponteiro";
        }
    }

    // exp_relacional: exp1 = exp_aritmetica (op_relacional exp2 =
    // exp_aritmetica)?;
    @Override
    public String visitExp_relacional(Exp_relacionalContext ctx) {
        String ret = visitChildren(ctx);

        // Caso haja algum operador relacional
        // O tipo da expressao passa a ser logico
        if (ctx.op_relacional() != null) {
            return "logico";
        } else {
            return ret;
        }
    }

    // parcela_logica: constante = ('verdadeiro' | 'falso') | exp_relacional;
    @Override
    public String visitParcela_logica(Parcela_logicaContext ctx) {
        String ret = visitChildren(ctx);
        if (ctx.constante != null) {
            return "logico";
        } else {
            return ret;
        }
    }
}