package br.ufscar.dc.compilador.semantico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TabelaDeSimbolos {
    private String escopo; // Nome do escopo referente a tabela
    private ArrayList<EntradaTabelaDeSimbolos> simbolos;

    public TabelaDeSimbolos(String escopo) {
        this.escopo = escopo;
        this.simbolos = new ArrayList<>();
    }

    public void adicionarSimbolo(String nome, String tipo, String tipoEntrada, ArrayList<String> listParam,
            TabelaDeSimbolos tabelaRegistro) {
        simbolos.add(new EntradaTabelaDeSimbolos(nome, tipo, tipoEntrada, listParam, tabelaRegistro));
    }

    public EntradaTabelaDeSimbolos getEntradaSimbolo(String nome) {
        for (EntradaTabelaDeSimbolos simbolo : simbolos) {
            if (simbolo.getNome().equals(nome)) {
                return simbolo;
            }
        }
        return null;
    }

    // Verifica se um determinado simbolo existe na tabela
    boolean temSimbolo(String nome) {
        if (nome.contains(".")) {
            // Registro
            ArrayList<String> toks = new ArrayList<>(Arrays.asList(nome.split("\\.")));
            TabelaDeSimbolos tabelaRegistro;

            // Recupera a entrada do registro na tabela
            // O primeiro elemento de toks e o nome da varaivel cujo tipo e registro
            EntradaTabelaDeSimbolos varRegistro = this.getEntradaSimbolo(toks.get(0));

            if (varRegistro.getTabelaRegistro() != null) {
                // Declarado o registro na variavel
                tabelaRegistro = varRegistro.getTabelaRegistro();
            } else {
                // Declarado o registor como tipo
                // Recupera a entrada do registro na tabela
                EntradaTabelaDeSimbolos registro = this.getEntradaSimbolo(varRegistro.getTipo());
                tabelaRegistro = registro.getTabelaRegistro();
            }

            // Busca na tabela de simbolos do registro, o simbolo
            // Ignorando o primeiro identificador antes do ponto, por ser o nome
            // da varaivel de tipo registro
            return tabelaRegistro.temSimbolo(String.join(".", toks.stream().skip(1).collect(Collectors.toList())));
        } else {
            for (EntradaTabelaDeSimbolos simbolo : simbolos) {
                if (simbolo.equals(nome)) {
                    return true;
                }
            }
            return false;
        }
    }

    // Verifica se um determinado tipo existe na tabela
    boolean temTipo(String nome) {
        for (EntradaTabelaDeSimbolos tipo : simbolos) {
            if (tipo.equals(nome) && tipo.getTipoEntrada().equals(("tipo"))) {
                return true;
            }
        }
        return false;
    }

    // Retorna o tipo de um simbolo pelo nome
    public String getTipoPorNome(String nome) {
        if (nome.contains(".")) {
            ArrayList<String> toks = new ArrayList<>(Arrays.asList(nome.split("\\.")));
            TabelaDeSimbolos tabelaRegistro;

            EntradaTabelaDeSimbolos varRegistro = this.getEntradaSimbolo(toks.get(0));

            if (varRegistro.getTabelaRegistro() != null) {
                tabelaRegistro = varRegistro.getTabelaRegistro();
            } else {
                EntradaTabelaDeSimbolos registro = this.getEntradaSimbolo(varRegistro.getTipo());
                tabelaRegistro = registro.getTabelaRegistro();
            }
            return tabelaRegistro.getTipoPorNome(String.join(".", toks.stream().skip(1).collect(Collectors.toList())));
        } else {
            for (EntradaTabelaDeSimbolos simbolo : simbolos) {
                if (simbolo.equals(nome)) {
                    return simbolo.getTipo();
                }
            }
        }
        return null;
    }
}
