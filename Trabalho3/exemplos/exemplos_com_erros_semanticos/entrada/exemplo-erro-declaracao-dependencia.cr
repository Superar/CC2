Cronograma ExemploDependencias {
    Atividades {
        Atividade dependenciaDeclarada {
            "Esta atividade tem suas dependências declaradas",
            Datas {
                01.02.2016 - 31.03.2016
            },
            Depende {
                dependenciaNaoDeclarada
            }
        },
        Atividade dependenciaNaoDeclarada {
            "Esta atividade tem sua dependencia nao declarada",
            Datas {
                01.12.2015 - 25.12.2015
            },
            Depende {
                dependenciaFantasma
            }
        }
    }
    Configuracao {
        Linhas horizontais,
        Linhas verticais,
        Formato: dd.mm.yyyy
    }
}
