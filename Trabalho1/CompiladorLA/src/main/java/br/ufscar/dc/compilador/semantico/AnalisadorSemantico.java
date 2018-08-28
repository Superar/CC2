package br.ufscar.dc.compilador.semantico;

import br.ufscar.dc.antlr.LABaseVisitor;
import br.ufscar.dc.antlr.LAParser.*;
import br.ufscar.dc.compilador.erros.ErroSemantico;

public class AnalisadorSemantico extends LABaseVisitor<String> {
    PilhaDeTabelas escopos;
    public ErroSemantico erros;

    public AnalisadorSemantico() {
        super();
        escopos = new PilhaDeTabelas();
        erros = new ErroSemantico();
    }

    // programa: declaracoes 'algoritmo' corpo 'fim_algoritmo';
    @Override
    public String visitPrograma(ProgramaContext ctx) {
        // Cria o escopo global
        TabelaDeSimbolos escopoGlobal = new TabelaDeSimbolos("global");
        // Adiciona os tipos básicos
        escopoGlobal.adicionarSimbolo("literal", "tipo");
        escopoGlobal.adicionarSimbolo("inteiro", "tipo");
        escopoGlobal.adicionarSimbolo("real", "tipo");
        escopoGlobal.adicionarSimbolo("logico", "tipo");
        // Adiciona tipos de ponteiro
        escopoGlobal.adicionarSimbolo("^literal", "tipo");
        escopoGlobal.adicionarSimbolo("^inteiro", "tipo");
        escopoGlobal.adicionarSimbolo("^real", "tipo");
        escopoGlobal.adicionarSimbolo("^logico", "tipo");
        escopos.empilha(escopoGlobal);

        visitChildren(ctx);

        escopos.desempilha();
        return null;
    }

    /*
     * declaracao_local: 'declare' variavel | 'constante' IDENT ':' tipo_basico '='
     * valor_constante | 'tipo' IDENT ':' tipo;
     */
    @Override
    public String visitDeclaracao_local(Declaracao_localContext ctx) {

        // Declaracao de constante
        if (ctx.valor_constante() != null) {
            if (!escopos.temSimbolo(ctx.IDENT().getText())) {
                // Adiciona na tabela de simbolos do escopo
                escopos.topo().adicionarSimbolo(ctx.IDENT().getText(), ctx.tipo_basico().getText());
            } else {
                erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": identificador " + ctx.IDENT().getText()
                        + " ja declarado anteriormente");
            }
        }

        // Declaracao de tipo
        if (ctx.tipo() != null) {
            if (!escopos.temTipo(ctx.IDENT().getText())) {
                escopos.topo().adicionarSimbolo(ctx.IDENT().getText(), "tipo");
            } else {
                erros.adicionarErro("Linha " + ctx.getStart().getLine());
            }
        }

        return visitChildren(ctx);
    }

    // variavel: primeiroIdentificador = identificador (',' listaIdentificador += identificador)* ':' tipo;
    @Override
    public String visitVariavel(VariavelContext ctx) {
        if (!escopos.temSimbolo(ctx.primeiroIdentificador.getText())) {
            // adiciona primeiro identificador ao escopo
            escopos.topo().adicionarSimbolo(ctx.primeiroIdentificador.getText(), ctx.tipo().getText());
        } else {
            erros.adicionarErro("Linha " + ctx.primeiroIdentificador.getStart().getLine() + ": identificador "
                    + ctx.primeiroIdentificador.getText() + " ja declarado anteriormente");
        }

        // Se existirem outros identificadores, adiciona ao escopo
        if (ctx.listaIdentificador != null) {
            for (IdentificadorContext identificador : ctx.listaIdentificador) {
                if (!escopos.temSimbolo(identificador.getText())) {
                    escopos.topo().adicionarSimbolo(identificador.getText(), ctx.tipo().getText());
                } else {
                    erros.adicionarErro("Linha " + identificador.getStart().getLine() + ": identificador "
                            + identificador.getText() + " ja declarado anteriormente");
                }
            }
        }

        return visitChildren(ctx);
    }

    // tipo_basico_ident: tipo_basico | IDENT;
    @Override
    public String visitTipo_basico_ident(Tipo_basico_identContext ctx) {
        if (ctx.IDENT() != null) {
            if (!escopos.temTipo(ctx.IDENT().getText())) {
                erros.adicionarErro(
                        "Linha " + ctx.getStart().getLine() + ": tipo " + ctx.IDENT().getText() + " nao declarado");
            }
        }
        return visitChildren(ctx);
    }

    /*
     * cmdLeia: 'leia' '(' ('^')? primeiroIdentificador = identificador ( ',' ('^')?
     * listaIdentificador += identificador )* ')';
     */
    @Override
    public String visitCmdLeia(CmdLeiaContext ctx) {
        // TODO: Verificar identificadores de registro corretamente. Caso de teste:
        // 12

        if (!escopos.temSimbolo(ctx.primeiroIdentificador.getText())) {
            erros.adicionarErro("Linha " + ctx.primeiroIdentificador.getStart().getLine() + ": identificador "
                    + ctx.primeiroIdentificador.getText() + " nao declarado");
        }

        if (ctx.listaIdentificador != null) {
            for (IdentificadorContext identificador : ctx.listaIdentificador) {
                if (!escopos.temSimbolo(identificador.getText())) {
                    erros.adicionarErro("Linha " + identificador.getStart().getLine() + ": identificador "
                            + identificador.getText() + " nao declarado");

                }
            }
        }

        return visitChildren(ctx);
    }

    /*
     * parcela_unario: ('^')? identificador | IDENT '(' expressao (',' expressao)*
     * ')' | NUM_INT | NUM_REAL | '(' expressao ')';
     */
    @Override
    public String visitParcela_unario(Parcela_unarioContext ctx) {
        if (ctx.identificador() != null) {
            if (!escopos.temSimbolo(ctx.identificador().getText())) {
                erros.adicionarErro("Linha " + ctx.identificador().getStart().getLine() + ": identificador "
                        + ctx.identificador().getText() + " nao declarado");
            }
        }

        return visitChildren(ctx);
    }
}