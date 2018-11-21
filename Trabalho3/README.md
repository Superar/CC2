# Trabalho 3 - Construção de Compiladores 2

Trabalho 3 para a Disciplina de Construção de Compiladores 2, implementado pelo grupo:

- Marcio Lima Inácio - RA: 587265;
- Pedro Henrique Dumont Mauad - RA: 619736;
- Camila Araujo - RA: 587060;
- Mayara Ap. S. Amaral Diogo - RA: 587176

Este trabalho consiste no projeto e implementação de um compilador de uma linguagem concebida pelo grupo incluindo análise léxica, análise sintática e geração de código.

A linguagem proposta se chama **Chronological** e tem como objetivo a geração de cronogramas em PDF. Os cronogramas são representados por tabelas e gráficos de Gantt. São gerados códigos em LaTeX que podem ser compilados em PDF posteriormente.

Todas as informações a seguir, sobre instalação, compilação e execução foram executadas em máquinas com Ubuntu versão 18.04.1 LTS.

## Pré-requisitos

Para compilar e executar o Compilador, é necessário possuir as seguintes ferramentas instaladas:

- [Java, versão 8 ou maior](http://openjdk.java.net/install/);
- [Apache Maven versão 3.5.2 ou maior](https://maven.apache.org/)
- [pdfTeX versão 3.14159265-2.6-1.40.17 ou maior](http://www.tug.org/texlive/)

Também são necessários alguns pacotes LaTeX:

- [longtable](https://ctan.org/pkg/longtable)
- [colortbl](https://ctan.org/pkg/colortbl)
- [booktabs](https://ctan.org/pkg/booktabs)
- [pgfgantt](https://ctan.org/pkg/pgfgantt)
- [pdflscape](https://ctan.org/pkg/pdflscape)
- [geometry](https://ctan.org/pkg/geometry)

Todos os pacotes estão disponíveis na distribuição [TeX Live](http://www.tug.org/texlive/) ou em ferramentas online como [Overleaf](https://www.overleaf.com/).

## Estrutura do projeto

O projeto consiste de arquivos de exemplos e arquivos do compilador em si. Os arquivos de exemplo se encontram na pasta `exemplos` enquanto a implmentação do compilador está na pasta `chronological`. Existem também _scripts_ auxiliares `gera_pdf.sh`, `gera_tex.sh` e `testar_arquivo.sh` para facilitar a execução do compilador.

A estrutura do projeto Java segue a estrutura básica de um projeto Maven. As informações sobre o projeto estão indicadas no arquio `pom.xml`. A pasta `src/antlr4` contém a gramática da linguagem no formato Antlr4 e a pasta `src/java` contém os códigos-fonte Java usados para implementar o compilador.

O arquivo `.jar` executável será gerado na pasta `target`. Qualquer saída gerada pelos _scripts_ `gera_pdf.sh`, `gera_tex.sh` e `testar_arquivo.sh` será produzida em uma pasta `saidaGerada`.

A estrutura de pastas e arquivos está indicada abaixo:

```
Trabalho1
│
│   gera_pdf.sh
│   gera_tex.sh
│   testar_arquivo.sh
│   lista_exemplos.txt
│   README.md
│
└───exemplos
│
└───chronological
│   │   pom.xml
│   │
│   └───src
│   │   │
│   │   └───main
│   │   │   |
│   │   |   └───antlr4
│   │   |   └───java
│   │
│   └───target
│
└───saidaGerada
```

## Como compilar

Para compilar o projeto Maven, basta executar, dentro do diretório `chronological`, o seguinte comando:

```bash
mvn clean package
```

Um arquivo `chronological-0.1-jar-with-dependencies.jar` será gerado no diretório `target`, como indicado acima.

## Como executar

Para executar o compilador, deve-se executar o seguinte comando:

```bash
java -jar chronological/target/chronological-0.1-jar-with-dependencies.jar
```

A execução espera alguns parâmetros:

- Argumento 1: Arquivo a ser compilado (obrigatório);
- Argumento 2: Arquivo de saída (opcional)

## Arquivos adicionais

Alguns arquivos _bash_ foram implementados para facilitar as tarefas de compilação e execução do projeto.

O _script `gera_tex.sh` recebe um parâmetro `-f`, correspondente ao caminho do arquivo implementado em Chronological a ser compilado. A saída será um arquivo `.tex` na pasta `saidaGerada`. O código gerado também é exibido na tela. Este arquivo adicionalmente executa o processo de compilação do projeto indicado anteriormente, apenas se o arquivo `chronological-0.1-jar-with-dependencies.jar` não estiver presente no diretório `target`.

O _script_ `gera_pdf.sh` recebe um parâmetro `-f`, correspondente ao caminho do arquivo `.tex` a ser compilado. A saída será um arquivo PDF na pasta `saidaGerada`. O _script_ também realiza verificações de instalação da ferramenta `pdfTeX` e dos pacotes necessários. 

O _script_ `testar_arquivo.sh`, recebe um parâmetro `-f`, correspondente ao caminho do arquivo em Chronological a ser compilado. Este arquivo executa, em sequência, os _scripts_ `gera_tex.sh` e `gera_pdf.sh`, criando um PDF diretamente de um arquivo em Chronological.

Todos os _scripts_ devem ser executados dentro do diretório `Trabalho3` do projeto, e necessitam da estrutura de pastas e arquivos indicada anteriormente.

## Como programar em Chronological

A linguagem prossui algumas estruturas básicas:

- Cronogramas
- Atividades
- Datas
- Configurações

Todo arquivo deve necessariamente possuir um cronograma implementado, porém não há limite de cronogramas em um único arquivo, todo cronograma deve possuir um nome único. Cada componente citado acima é delimitado por chaves `{` e `}`.

```
Cronograma cronogramaA {
    ...
}

Cronograma cronogramaB {
    ...
}
```

Cada cronograma é composto de atividades, sendo delimitadas por uma palavra chave `Atividades` e as chaves. Dentro do componente de atividades há a implementação de cada atividade a ser incluída ao cronograma, indicadas pela palavra chave `Atividade` e as chaves. Múltiplas atividades são separadas por vírgulas.

```
Cronograma cronogramaA {
    Atividades {
        Atividade atividade1 {
            ...
        }
    }
    ...
}
```

Toda atividade deve possuir, também, um nome único dentro do mesmo cronograma. Dentro da atividade, é necessário a presença de dois componentes obrigatórios:

- Descrição
- Datas
  
A descrição é indicada por aspas duplas `"descricao"` e as datas possuem o seu componente próprio `Datas`. Dentro do componente `Datas` são indicados os períodos em que a atividade será realizada no formato `dataInicial - dataFinal`.

Cada informação das atividades (Descrição, Datas e Dependências) é separada por vírgula.

```
Cronograma cronogramaA {
    Atividades {
        Atividade atividade1 {
            "Esta é a descrição da atividade1",
            Datas {
                23/09/2018 - 30/09/2018
            }
        }
    }
}
```

Uma mesma atividade pode possuir diversas datas de realização, como mostra o exemplo `exemplo-duas-barras.cr` na pasta de exemplos. O formato das datas a serem utilizadas na descrição dos períodos seque o formato padrão `dd/mm/yyyy`, podendo ser alterado por meio das configurações.

### Configurações

As configurações são diversas:

- **Linhas horizontais** - Mostra linhas horizontais no diagrama gerado
- **Linhas verticais** - Mostra linhas verticais no diagrama gerado
- **Cor** - Altera a cor das barras no diagrama
- **Altura da barra** - Altera a altura das barras no diagrama
- **Formato** - Altera o formado de data utilizado
- **Mostrar dias** - Mostra os dias no diagrama gerado

Estas configurações são opcionais e podem ser aplicadas apenas ao cronograma, por meio da palavra chave `Configuracao` e as chaves. Múltiplas configurações são separadas por vírgula.

```
Cronograma cronogramaA {
    Atividades {
        ...
    }
    Configuracao {
        ...
    }
}
```

Algumas das configurações podem ser aplicadas também a atividades, sendo limitado às configurações de:

- **Cor** - Altera a cor das barras da atividade específica no diagrama
- **Altura da barra** - Altera a altura das barras da atividade específica no diagrama

O arquivo `exemplo-configuracao-local.cr` apresenta um exemplo de uso de configurações em atividades.

Algumas configurações possuem parâmetros indicados pelo nome da configuração seguido de dois pontos `:`.

A configuração de `cor` aceita valores de cor em formato HTML `0xFFFFFF`, indicado por `0x`, e em RGB `(255,255,255)`, indicado pelos parênteses e separados por vírgula.

A altura da barra recebe um número inteiro ou real, com separador de casas decimais sendo o ponto.

O formato de data é indicado pelas informações: `dd` (dia), `mm` (mês) e `yyyy` (ano), separados pelos separadores válidos: ponto `.`, barra `/` e hífen `-`. Todos os separadores em um formato de data devem ser iguais.

São válidos apenas três padrões de data:

- Little-endian - dd/mm/yyyy
- Mid-endian - mm/dd/yyyy
- Big-endian - yyyy/mm/dd

Exemplos de formatos de data válidos são: 

- dd/mm/yyyy
- yyyy-mm-dd
- mm.dd.yyyy

Exemplos de formatos de data inválidos são:

- dd/mm.yyyy
- yyyy.mm-dd
- mm.dd-yyyy

### Dependências

As atividades em Chronological podem possui dependências entre si, sendo representado por setas entre as barras do diagrama. Para indicar a dependência de uma atividade com a outra, é necessário a palavra chave `Depende` e as chaves.

```
Cronograma {
    Atividades {
        Atividade AtividadeDependencia {
            ...
        },
        Atividade AtividadeDependente {
            ...
            Depende {
                AtividadeDependencia
            }
        }
    }
}
```

Um exemplo com dependência é mostrado no arquivo `exemplo-com-dependencias.cr` na pasta de exemplos sem erros.

Para que uma dependência seja realizada com sucesso há algumas condições:

- A dependência está declarada no mesmo cronograma
- A dependência ocorre antes do primeiro período da dependente
  
Todas as verificações são realizadas com relação ao primeiro período da atividade dependente. Caso a atividade dependência possua apenas um período, este será utilizado para a geração da dependência, caso contrário é necessário explicitar o período do qual se depende. Esta explicitação pode ser observada no exemplo `exemplo-com-multiplas-dependencias.cr`.