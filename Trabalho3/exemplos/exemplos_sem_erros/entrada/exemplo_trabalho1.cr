Cronograma Trabalho1 {
    Atividades {
        Atividade EscreverGramatica {
            "Escrever a gramática de acordo com as especificações
            no documento PDF disponibilizado pelo professor.",
            Datas {
                09/08/2018 - 09/08/2018
            }
        },
        Atividade TratarErrosLexicos {
            "Fazer o tratamento dos erros léxicos de acordo com os
            exemplos disponibilizados pelo professor.",
            Datas {
                13/08/2018 - 15/08/2018
            },
            Depende {
                EscreverGramatica
            }
        },
        Atividade TratarErrosSintaticos {
            "Fazer o tratamento dos erros sintáticos de acordo com os
            exemplos disponibilizados pelo professor.",
            Datas {
                15/08/2018 - 15/08/2018
            },
            Depende {
                TratarErrosLexicos
            }
        },
        Atividade TratarErrosSemanticos {
            "Fazer o tratamento de erros semânticos de acordo com os
            exemplos disponibilizados pelo professor.",
            Datas {
                16/08/2018 - 02/09/2018
            },
            Depende {
                TratarErrosSintaticos
            }
        },
        Atividade GeracaoDeCodigo {
            "Implementar a geração de código em C a partir de uma entrada
            em LA.",
            Datas {
                07/09/2018 - 17/09/2018
            },
            Depende { 
                TratarErrosSemanticos
            }
        },
        Atividade Documentacao {
            "Revisar o código, comentar trechos importantes e descrever o
            processo de compilação e execução do compilador em um arquivo README.
            O projeto do compilador juntamente com os documentos gerados devem ser
            enviados via AVA.",
            Datas {
                19/09/2018 - 19/09/2018
            }
        }
    }
    Configuracao {
        Mostrar dias,
        Linhas verticais
    }
}