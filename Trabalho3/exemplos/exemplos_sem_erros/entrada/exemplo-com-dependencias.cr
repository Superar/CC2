Cronograma FrufflesExemplo {
    Atividades {
        Atividade Atividade1 {
            "Esta é a descrição da atividade 1.",
            Datas {
                01.02.2016 - 31.03.2016
            },
            Configuracao {
                Cor: (0, 0, 255)
            }
        },
        Atividade Atividade2 {
            "Este texto descreve o que será realizado
            na atividade 2",
            Datas {
                01.04.2016 - 31.07.2016
            },
            Configuracao {
                Cor: (255, 0, 0)
            },
            Depende {
                Atividade1
            }
        },
        Atividade Atividade3 {
            "Texto da atividade 3",
            Datas {
                01.01.2016 - 30.04.2016
            }
        }
    }
    Configuracao {
        Linhas horizontais,
        Linhas verticais,
        Formato: dd.mm.yyyy
    }
}
