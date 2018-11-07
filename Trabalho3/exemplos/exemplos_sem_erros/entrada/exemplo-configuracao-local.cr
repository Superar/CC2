Cronograma ExemploConfiguracaoLocal {
    Atividades {
        Atividade NomeDaAtividade1 {
            "Descrição da atividade 1",
            Datas {
                23/09/2018 - 30/09/2018
                05/10/2018 - 08/10/2018
            }
        },
        Atividade NomeDaAtividade2 {
            "Descrição da atividade 2",
            Datas {
                23/09/2018 - 30/09/2018
            },
            Configuracao {
                Cor: 0xA3B1FF
            }
        },
        Atividade NomeDaAtividade3 {
            "Descrição da atividade 3",
            Datas {
                03/10/2018 - 03/10/2018
            },
            Depende {
                NomeDaAtividade2
            }
        }
    }
    Configuracao {
        Linhas horizontais,
        Linhas verticais,
        Mostrar dias,
        Cor: (0, 225, 0),
        Altura da barra: 0.5
    }
}
