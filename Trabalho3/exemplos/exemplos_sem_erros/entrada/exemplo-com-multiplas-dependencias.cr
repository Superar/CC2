Cronograma ExemploMultiplasDependencias {
    Atividades {
        Atividade Atividade1 {
            "Esta é a descrição da atividade 1.",
            Datas {
                01.02.2016 - 01.03.2016
                01.07.2016 - 01.09.2016
            },
            Configuracao {
                Cor: (0, 0, 255)
            }
        },
        Atividade Atividade2 {
            "Este texto descreve o que será realizado
            na atividade 2",
            Datas {
                01.06.2016 - 01.10.2016
            },
            Depende {
                Atividade1.1
            }
        }
    }
    Configuracao {
        Linhas horizontais,
        Linhas verticais,
        Formato: dd.mm.yyyy
    }
}
