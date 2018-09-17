package br.ufscar.dc.compilador.semantico;

import java.util.Arrays;
import java.util.Collections;

import org.antlr.v4.runtime.Token;

import java.util.ArrayList;

import br.ufscar.dc.antlr.LABaseVisitor;
import br.ufscar.dc.antlr.LAParser.*;
import br.ufscar.dc.compilador.erros.ErroSemantico;

public class AnalisadorSemantico extends LABaseVisitor<String> {
    PilhaDeTabelas escopos;
    public ErroSemantico erros;
    ArrayList<String> listParamCriada = new ArrayList<>();

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
        escopoGlobal.adicionarSimbolo("literal", null, "tipoBasico", null, null);
        escopoGlobal.adicionarSimbolo("inteiro", null, "tipoBasico", null, null);
        escopoGlobal.adicionarSimbolo("real", null, "tipoBasico", null, null);
        escopoGlobal.adicionarSimbolo("logico", null, "tipoBasico", null, null);
        // Adiciona tipos de ponteiro
        escopoGlobal.adicionarSimbolo("^literal", null, "tipoBasico", null, null);
        escopoGlobal.adicionarSimbolo("^inteiro", null, "tipoBasico", null, null);
        escopoGlobal.adicionarSimbolo("^real", null, "tipoBasico", null, null);
        escopoGlobal.adicionarSimbolo("^logico", null, "tipoBasico", null, null);
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
                escopos.topo().adicionarSimbolo(ctx.IDENT().getText(), ctx.tipo_basico().getText(), "tipoBasico", null,
                        null);
            } else {
                erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": identificador " + ctx.IDENT().getText()
                        + " ja declarado anteriormente");
            }
        }

        // Declaracao de tipo
        if (ctx.tipo() != null) {
            if (!escopos.temTipo(ctx.IDENT().getText())) {
                if (ctx.tipo().registro() != null) {
                    // Declaracao de registro
                    // Precisa criar a tabela de simbolos do registro
                    TabelaDeSimbolos tabelaRegistro = new TabelaDeSimbolos(ctx.IDENT().getText());

                    // Adiciona cada variavel na tabela do registro
                    for (VariavelContext var : ctx.tipo().registro().variavel()) {
                        tabelaRegistro.adicionarSimbolo(var.primeiroIdentificador.getText(), var.tipo().getText(),
                                "variavel", null, null);
                        for (IdentificadorContext ident : var.listaIdentificador) {
                            tabelaRegistro.adicionarSimbolo(ident.getText(), var.tipo().getText(), "variavel", null,
                                    null);
                        }
                    }
                    escopos.topo().adicionarSimbolo(ctx.IDENT().getText(), null, "tipo", null, tabelaRegistro);
                } else {
                    // Declaracao de tipo estendido
                    escopos.topo().adicionarSimbolo(ctx.IDENT().getText(), null, "tipo", null, null);
                }
            } else {
                erros.adicionarErro("Linha " + ctx.getStart().getLine());
            }
        }

        return visitChildren(ctx);
    }

    // variavel: primeiroIdentificador = identificador (',' listaIdentificador +=
    // identificador)* ':' tipo;
    @Override
    public String visitVariavel(VariavelContext ctx) {
        TabelaDeSimbolos tabelaRegistro = null;
        if (ctx.tipo().registro() != null) {
            // Tipo e um registro
            // Precisa criar tabela de simbolo de registro
            tabelaRegistro = new TabelaDeSimbolos("registro");

            // Adiciona cada variavel na tabela do registro
            for (VariavelContext var : ctx.tipo().registro().variavel()) {
                tabelaRegistro.adicionarSimbolo(var.primeiroIdentificador.getText(), var.tipo().getText(), "variavel",
                        null, null);
                for (IdentificadorContext ident : var.listaIdentificador) {
                    tabelaRegistro.adicionarSimbolo(ident.getText(), var.tipo().getText(), "variavel", null, null);
                }
            }
        }

        if (!escopos.temSimbolo(ctx.primeiroIdentificador.primeiroIdent.getText())) {
            // adiciona primeiro identificador ao escopo
            escopos.topo().adicionarSimbolo(ctx.primeiroIdentificador.primeiroIdent.getText(), ctx.tipo().getText(),
                    "variavel", null, tabelaRegistro);
        } else {
            erros.adicionarErro("Linha " + ctx.primeiroIdentificador.getStart().getLine() + ": identificador "
                    + ctx.primeiroIdentificador.getText() + " ja declarado anteriormente");
        }

        // Se existirem outros identificadores, adiciona ao escopo
        if (ctx.listaIdentificador != null) {
            for (IdentificadorContext identificador : ctx.listaIdentificador) {
                if (!escopos.temSimbolo(identificador.primeiroIdent.getText())) {
                    escopos.topo().adicionarSimbolo(identificador.primeiroIdent.getText(), ctx.tipo().getText(),
                            "variavel", null, tabelaRegistro);
                } else {
                    erros.adicionarErro("Linha " + identificador.getStart().getLine() + ": identificador "
                            + identificador.getText() + " ja declarado anteriormente");
                }
            }
        }

        return visitChildren(ctx);
    }

    // identificador returns [String tipoVar]: primeiroIdent = IDENT ('.'
    // listaIdent
    // += IDENT)* dimensao;
    @Override
    public String visitIdentificador(IdentificadorContext ctx) {
        if (ctx.listaIdent.size() > 0) {
            // O tipo a ser usado sera o do ultimo identificador
            String ultimoIdent = ctx.listaIdent.get(ctx.listaIdent.size() - 1).getText();
            ctx.tipoVar = escopos.getTipoPorNome(ultimoIdent);
        } else {
            // So um identificador
            ctx.tipoVar = escopos.getTipoPorNome(ctx.primeiroIdent.getText());
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
     * declaracao_global: 'procedimento' IDENT '(' (parametros)? ')'
     * (declaracao_local)* ( cmd )* 'fim_procedimento' | 'funcao' IDENT '('
     * (parametros)? ')' ':' tipo_estendido ( declaracao_local )* (cmd)*
     * 'fim_funcao';
     */
    @Override
    public String visitDeclaracao_global(Declaracao_globalContext ctx) {
        // Constroi a lista de tipos dos parametros do procedimento ou funcao
        ArrayList<String> parametros = new ArrayList<>();
        for (ParametroContext param : ctx.parametros().parametro()) {
            for (IdentificadorContext ident : param.identificador()) {
                parametros.add(param.tipo_estentido().getText());
            }
        }

        // Cria a entrada na tabela de simbolos da funcao ou procedimento
        if (ctx.tipo_estentido() == null) {
            escopos.topo().adicionarSimbolo(ctx.IDENT().getText(), null, "funcao", parametros, null);
        } else {
            escopos.topo().adicionarSimbolo(ctx.IDENT().getText(), ctx.tipo_estentido().getText(), "funcao", parametros,
                    null);
        }

        // Cria o escopo da funcao ou procedimento
        TabelaDeSimbolos escopo = new TabelaDeSimbolos(ctx.IDENT().getText());
        escopos.empilha(escopo);

        // Se for um procedimento, verifica se possui cmdRetorne
        if (ctx.cmdProcedimento != null) {
            for (CmdContext cmd : ctx.cmdProcedimento) {
                if (cmd.cmdRetorne() != null) {
                    erros.adicionarErro(
                            "Linha " + cmd.getStart().getLine() + ": comando retorne nao permitido nesse escopo");
                }
            }
        }

        String ret = visitChildren(ctx);

        escopos.desempilha();
        return ret;
    }

    /*
     * parametro: ('var')? primeiroIdentificador = identificador ( ','
     * listaIdentificador += identificador )* ':' tipo_estentido;
     */
    @Override
    public String visitParametro(ParametroContext ctx) {
        // Tabela de simbolos do tipo correspondente
        TabelaDeSimbolos tabelaRegistro = escopos.getEntradaPorNome(ctx.tipo_estentido().getText()).getTabelaRegistro();

        // Adiciona cada parametro ao escopo da funcao que deve estar no topo da
        // pilha
        if (!escopos.temSimbolo(ctx.primeiroIdentificador.getText())) {
            escopos.topo().adicionarSimbolo(ctx.primeiroIdentificador.getText(), ctx.tipo_estentido().getText(),
                    "variavel", null, tabelaRegistro);
        }

        if (ctx.listaIdentificador != null) {
            for (IdentificadorContext identificador : ctx.listaIdentificador) {
                if (!escopos.temSimbolo(identificador.getText())) {
                    escopos.topo().adicionarSimbolo(identificador.getText(), ctx.tipo_estentido().getText(), "variavel",
                            null, tabelaRegistro);
                }
            }
        }

        return visitChildren(ctx);
    }

    // corpo: (declaracao_local)* (cmd)*;
    @Override
    public String visitCorpo(CorpoContext ctx) {

        // Verifica se ha comando retorne no escopo de algoritmo
        if (ctx.cmd() != null) {
            for (CmdContext cmd : ctx.cmd()) {
                if (cmd.cmdRetorne() != null) {
                    erros.adicionarErro(
                            "Linha " + cmd.getStart().getLine() + ": comando retorne nao permitido nesse escopo");
                }
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
        String ident = ctx.primeiroIdentificador.primeiroIdent.getText();
        for (Token tok : ctx.primeiroIdentificador.listaIdent) {
            ident += ".";
            ident += tok.getText();
        }

        if (!escopos.temSimbolo(ident)) {
            erros.adicionarErro("Linha " + ctx.primeiroIdentificador.getStart().getLine() + ": identificador " + ident
                    + " nao declarado");
        }

        if (ctx.listaIdentificador != null) {
            for (IdentificadorContext identificador : ctx.listaIdentificador) {
                ident = identificador.primeiroIdent.getText();
                for (Token tok : identificador.listaIdent) {
                    ident += ".";
                    ident += tok.getText();
                }

                if (!escopos.temSimbolo(ident)) {
                    erros.adicionarErro("Linha " + identificador.getStart().getLine() + ": identificador " + ident
                            + " nao declarado");
                }
            }
        }

        return visitChildren(ctx);
    }

    // cmdAtribuicao: (ponteiro = '^')? identificador '<-' expressao;
    @Override
    public String visitCmdAtribuicao(CmdAtribuicaoContext ctx) {
        if (!escopos.temSimbolo(ctx.identificador().primeiroIdent.getText())) {
            erros.adicionarErro("Linha " + ctx.identificador().getStart().getLine() + ": identificador "
                    + ctx.identificador().primeiroIdent.getText() + " nao declarado");
        }

        // Verifica o tipo do identificador e das expressoes
        String ret = visitChildren(ctx);
        ArrayList<String> tipos = new ArrayList<>(Arrays.asList(ret.split(",")));
        // Adiciona o tipo do identificador na lista de tipos
        tipos.add(0, escopos.getTipoPorNome(ctx.identificador().getText()));

        // Substitui valores
        // Operacoes entre inteiros e reais devem ser permitidas (sao numeros)
        Collections.replaceAll(tipos, "inteiro", "num");
        Collections.replaceAll(tipos, "real", "num");
        // Ponteiros devem receber enderecos
        Collections.replaceAll(tipos, "^inteiro", "ponteiro");

        // Se existe mais de um tipo na atribuicao, a operacao nao e compativel
        if (tipos.stream().distinct().limit(2).count() > 1) {
            // Simbolo sendo atribuido deve existir na lista de simbolos para gerar o
            // erro de
            if (escopos.temSimbolo(ctx.identificador().primeiroIdent.getText())) {
                erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": atribuicao nao compativel para "
                        + ctx.getText().split("<-")[0]);
            }
        }

        return null;
    }

    /*
     * parcela_unario: ('^')? identificador | IDENT '(' expressao (',' expressao)*
     * ')' | NUM_INT | NUM_REAL | '(' expressao ')';
     */
    @Override
    public String visitParcela_unario(Parcela_unarioContext ctx) {
        if (ctx.identificador() != null) {
            if (!escopos.temSimbolo(ctx.identificador().primeiroIdent.getText())) {
                erros.adicionarErro("Linha " + ctx.identificador().getStart().getLine() + ": identificador "
                        + ctx.identificador().getText() + " nao declarado");
            }
        }

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
            // Lista com os tipos dos parametros sendo usados na chamada
            ArrayList<String> parametros = new ArrayList<>();
            for (ExpressaoContext exp : ctx.expressao()) {
                parametros.add(escopos.getTipoPorNome(exp.getText().split("\\(|\\[")[0]));
            }

            // Verifica se os tipos dos parametros estao corretos
            if (!escopos.getEntradaPorNome(ctx.IDENT().getText()).getListParam().equals(parametros)) {
                erros.adicionarErro("Linha " + ctx.getStart().getLine()
                        + ": incompatibilidade de parametros na chamada de " + ctx.IDENT().getText());
            }
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

    @Override
    public String visitExpressao(ExpressaoContext ctx) {
        String ret = visitChildren(ctx);
        ctx.tipoVar = ret;
        return ret;
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
}