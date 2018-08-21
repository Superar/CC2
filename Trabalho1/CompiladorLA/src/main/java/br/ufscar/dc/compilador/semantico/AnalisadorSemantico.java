package br.ufscar.dc.compilador.semantico;

import org.antlr.v4.runtime.misc.ParseCancellationException;

import br.ufscar.dc.antlr.LABaseListener;
import br.ufscar.dc.antlr.LAParser.*;
import br.ufscar.dc.compilador.erros.ErroSemantico;

public class AnalisadorSemantico extends LABaseListener {

    PilhaDeTabelas escopos;
    public ErroSemantico erros;

    public AnalisadorSemantico(ProgramaContext arvore) {
        super();
        escopos = new PilhaDeTabelas();
        erros = new ErroSemantico();
        enterPrograma(arvore);
    }

    // programa: declaracoes 'algoritmo' corpo 'fim_algoritmo';
    @Override
    public void enterPrograma(ProgramaContext ctx) {
        // Cria o escopo global
        TabelaDeSimbolos escopoGlobal = new TabelaDeSimbolos("global");
        // Adiciona tipos básicos
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

        // Entra nos filhos
        enterDeclaracoes(ctx.declaracoes());
        enterCorpo(ctx.corpo());

        // Antes de sair da regra, desempilha o escopo
        escopos.desempilha();
    }

    // declaracoes: (decl_local_global)*;
    @Override
    public void enterDeclaracoes(DeclaracoesContext ctx) {
        // Verifica se há declaracao
        if (ctx.decl_local_global() != null) {
            // Entra em cada declaracao
            for (Decl_local_globalContext declaracao : ctx.decl_local_global()) {
                enterDecl_local_global(declaracao);
            }
        }
    }

    // decl_local_global: declaracao_local | declaracao_global;
    @Override
    public void enterDecl_local_global(Decl_local_globalContext ctx) {
        if (ctx.declaracao_local() != null) { // Declaracao local
            enterDeclaracao_local(ctx.declaracao_local());
        } else { // Declaracao global
            enterDeclaracao_global(ctx.declaracao_global());
        }
    }

    /*
     * declaracao_local: 'declare' variavel | 'constante' IDENT ':' tipo_basico '='
     * valor_constante | 'tipo' IDENT ':' tipo;
     */
    @Override
    public void enterDeclaracao_local(Declaracao_localContext ctx) {
        // Declaracao de variavel
        if (ctx.variavel() != null) {
            enterVariavel(ctx.variavel());
        }

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
                enterTipo(ctx.tipo());
            } else {
                // TODO: Arrumar erro correspondente
                erros.adicionarErro("Linha " + ctx.getStart().getLine());
            }
        }
    }

    // variavel: identificador (',' identificador)* ':' tipo;
    @Override
    public void enterVariavel(VariavelContext ctx) {
        // Verifica tipo
        enterTipo(ctx.tipo());

        if (!escopos.temSimbolo(ctx.primeiroIdentificador.getText())) {
            // Adiciona o primeiro identificador ao escopo
            escopos.topo().adicionarSimbolo(ctx.primeiroIdentificador.getText(), ctx.tipo().getText());
        } else {
            enterIdentificador(ctx.primeiroIdentificador);
        }

        // Se Existir outros identificadores, adiciona ao escopo
        if (ctx.listaIdentificador != null) {
            for (IdentificadorContext identificador : ctx.listaIdentificador) {
                if (!escopos.temSimbolo(identificador.getText())) {
                    escopos.topo().adicionarSimbolo(identificador.getText(), ctx.tipo().getText());
                } else {
                    enterIdentificador(identificador);
                }
            }
        }
    }

    @Override
    public void enterIdentificador(IdentificadorContext ctx) {
        if (escopos.temSimbolo(ctx.getText())) {
            erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": identificador " + ctx.getText()
                    + " ja declarado anteriormente");
        }
    }

    // tipo: registro | tipo_estentido;
    @Override
    public void enterTipo(TipoContext ctx) {
        if (ctx.registro() != null) {
            enterRegistro(ctx.registro());
        }

        if (ctx.tipo_estentido() != null) {
            enterTipo_estentido(ctx.tipo_estentido());
        }
    }

    // tipo_basico_ident: tipo_basico | IDENT;
    @Override
    public void enterTipo_basico_ident(Tipo_basico_identContext ctx) {
        if (ctx.IDENT() != null) {
            if (!escopos.temTipo(ctx.IDENT().getText())) {
                erros.adicionarErro(
                        "Linha " + ctx.getStart().getLine() + ": tipo " + ctx.IDENT().getText() + " nao declarado");
            }
        }
    }

    // tipo_estentido: ('^')? tipo_basico_ident;
    @Override
    public void enterTipo_estentido(Tipo_estentidoContext ctx) {
        enterTipo_basico_ident(ctx.tipo_basico_ident());
    }

    @Override
    public void enterRegistro(RegistroContext ctx) {
        super.enterRegistro(ctx);
        // TODO: Implementar os registros
    }

    // corpo: (declaracao_local)* (cmd)*;
    @Override
    public void enterCorpo(CorpoContext ctx) {
        // Declaracoes locais
        if (ctx.declaracao_local() != null) {
            for (Declaracao_localContext declaracao : ctx.declaracao_local()) {
                enterDeclaracao_local(declaracao);
            }
        }

        // Comandos
        if (ctx.cmd() != null) {
            for (CmdContext cmd : ctx.cmd()) {
                enterCmd(cmd);
            }
        }
    }

    /*
     * cmd: cmdLeia | cmdEscreva | cmdSe | cmdCaso | cmdPara | cmdEnquanto | cmdFaca
     * | cmdAtribuicao | cmdChamada | cmdRetorne;
     */
    @Override
    public void enterCmd(CmdContext ctx) {
        if (ctx.cmdLeia() != null) {
            enterCmdLeia(ctx.cmdLeia());
        }

        if (ctx.cmdEscreva() != null) {
            enterCmdEscreva(ctx.cmdEscreva());
        }

        if (ctx.cmdEnquanto() != null) {
            enterCmdEnquanto(ctx.cmdEnquanto());
        }

        if (ctx.cmdFaca() != null) {
            enterCmdFaca(ctx.cmdFaca());
        }

        if (ctx.cmdAtribuicao() != null) {
            enterCmdAtribuicao(ctx.cmdAtribuicao());
        }
    }

    /*
     * cmdLeia: 'leia' '(' ('^')? primeiroIdentificador = identificador ( ',' ('^')?
     * listaIdentificador += identificador )* ')';
     */
    @Override
    public void enterCmdLeia(CmdLeiaContext ctx) {
        // TODO: Verificar identificadores de registro corretamente. Caso de teste: 12
        
        if (!escopos.temSimbolo(ctx.primeiroIdentificador.getText())) {
            erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": identificador "
                    + ctx.primeiroIdentificador.getText() + " nao declarado");
        }

        if (ctx.listaIdentificador != null) {
            for (IdentificadorContext identificador : ctx.listaIdentificador) {
                if (!escopos.temSimbolo(identificador.getText())) {
                    erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": identificador "
                            + identificador.getText() + " nao declarado");
                }
            }
        }
    }

    // cmdEscreva: 'escreva' '(' primeiraExpressao = expressao (',' listaExpressao
    // += expressao)* ')';
    @Override
    public void enterCmdEscreva(CmdEscrevaContext ctx) {
        enterExpressao(ctx.primeiraExpressao);

        if (ctx.listaExpressao != null) {
            for (ExpressaoContext expressao : ctx.listaExpressao) {
                enterExpressao(expressao);
            }
        }
    }

    // cmdEnquanto: 'enquanto' expressao 'faca' (cmd)* 'fim_enquanto';
    @Override
    public void enterCmdEnquanto(CmdEnquantoContext ctx) {
        enterExpressao(ctx.expressao());

        if (ctx.cmd() != null) {
            for (CmdContext cmd : ctx.cmd()) {
                enterCmd(cmd);
            }
        }
    }

    // cmdFaca: 'faca' (cmd)* 'ate' expressao;
    @Override
    public void enterCmdFaca(CmdFacaContext ctx) {
        if (ctx.cmd() != null) {
            for (CmdContext cmd : ctx.cmd()) {
                enterCmd(cmd);
            }
        }

        enterExpressao(ctx.expressao());
    }

    // cmdAtribuicao: ('^')? identificador '<-' expressao;
    @Override
    public void enterCmdAtribuicao(CmdAtribuicaoContext ctx) {
        // TODO: Implementar verificacao de tipos
        super.enterCmdAtribuicao(ctx);
    }

    // exp_aritmetica: primeiroTermo = termo (op1 listaTermo += termo)*;
    @Override
    public void enterExp_aritmetica(Exp_aritmeticaContext ctx) {
        enterTermo(ctx.primeiroTermo);

        if (ctx.listaTermo != null) {
            for (TermoContext termo : ctx.listaTermo) {
                enterTermo(termo);
            }
        }
    }

    // termo: primeiroFator = fator (op2 listaFator += fator)*;
    @Override
    public void enterTermo(TermoContext ctx) {
        enterFator(ctx.primeiroFator);

        if (ctx.listaFator != null) {
            for (FatorContext fator : ctx.listaFator) {
                enterFator(fator);
            }
        }
    }

    // fator: primeiraParcela = parcela (op3 listaParcela += parcela)*;
    @Override
    public void enterFator(FatorContext ctx) {
        enterParcela(ctx.primeiraParcela);

        if (ctx.listaParcela != null) {
            for (ParcelaContext parcela : ctx.listaParcela) {
                enterParcela(parcela);
            }
        }
    }

    // parcela: (op_unario)? parcela_unario | parcela_nao_unario;
    @Override
    public void enterParcela(ParcelaContext ctx) {
        if (ctx.parcela_unario() != null) {
            enterParcela_unario(ctx.parcela_unario());
        }
    }

    /*
     * parcela_unario: ('^')? identificador | IDENT '(' expressao (',' expressao)*
     * ')' | NUM_INT | NUM_REAL | '(' expressao ')';
     */
    @Override
    public void enterParcela_unario(Parcela_unarioContext ctx) {
        if (ctx.identificador() != null) {
            if (!escopos.temSimbolo(ctx.identificador().getText())) {
                erros.adicionarErro("Linha " + ctx.getStart().getLine() + ": identificador "
                        + ctx.identificador().getText() + " nao declarado");
            }
        }
    }

    // exp_relacional: exp1 = exp_aritmetica (op_relacional exp2 =
    // exp_aritmetica)?;
    @Override
    public void enterExp_relacional(Exp_relacionalContext ctx) {
        enterExp_aritmetica(ctx.exp1);

        if (ctx.exp2 != null) {
            enterExp_aritmetica(ctx.exp2);
        }
    }

    // expressao: primeiroTermo = termo_logico (op_logico_1 listaTermo +=
    // termo_logico)*;
    @Override
    public void enterExpressao(ExpressaoContext ctx) {
        enterTermo_logico(ctx.primeiroTermo);

        if (ctx.listaTermo != null) {
            for (Termo_logicoContext termo : ctx.listaTermo) {
                enterTermo_logico(termo);
            }
        }
    }

    // termo_logico: primeiroFator = fator_logico (op_logico_2 listaFator +=
    // fator_logico)*;
    @Override
    public void enterTermo_logico(Termo_logicoContext ctx) {
        enterFator_logico(ctx.primeiroFator);

        if (ctx.listaFator != null) {
            for (Fator_logicoContext fator : ctx.listaFator) {
                enterFator_logico(fator);
            }
        }
    }

    // fator_logico: ('nao')? parcela_logica;
    @Override
    public void enterFator_logico(Fator_logicoContext ctx) {
        enterParcela_logica(ctx.parcela_logica());
    }

    // parcela_logica: ('verdadeiro' | 'falso') | exp_relacional;
    @Override
    public void enterParcela_logica(Parcela_logicaContext ctx) {
        if (ctx.exp_relacional() != null) {
            enterExp_relacional(ctx.exp_relacional());
        }
    }
}