Cronograma ExemploDependenciaSemPeriodo {
    Atividades {
        Atividade Dependencia {
            "Esta atividade é a dependência de outra",
            Datas {
                01.02.2016 - 31.03.2016
                05.04.2016 - 14.04.2016
            }
        },
        Atividade dependenciaComPeriodo {
            "Esta atividade tem sua dependência com o período especificado",
            Datas {
                05.04.2016 - 13.04.2016
            },
            Depende {
                Dependencia.1
            }
        },
        Atividade dependenciaSemPeriodo {
            "Esta atividade não tem sua dependência com o período especificado",
            Datas {
                05.04.2016 - 13.04.2016
            },
            Depende {
                Dependencia
            }
        },
        Atividade dependenciaComPeriodoFantasma {
            "Esta atividade depende de um período que não existe",
            Datas {
                05.04.2016 - 13.04.2016
            },
            Depende {
                Dependencia.125
            }
        },
        Atividade dependenciaAconteceAntes {
            "Esta atividade depende de uma atividade que ocorre depois dela",
            Datas {
                01.02.2016 - 31.03.2016
                12.06.2016 - 15.06.2016
            },
            Depende {
                Dependencia.2
            }
        }
    }
    Configuracao {
        Linhas horizontais,
        Linhas verticais,
        Formato: dd.mm.yyyy
    }
}
