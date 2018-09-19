# Trabalho 1 - Construção de Compiladores 2
Trabalho 1 para a Disciplina de Construção de Compiladores 2, implementado pelo grupo:

- Marcio Lima Inácio - RA: 587265;
- Pedro Henrique Dumont Mauad - RA: 619736

Este trabalho consiste na implementação de um compilador para a linguagem de programação LA, incluindo as etapas de análise léxica, sintática e geração de código.

Todas as informações a seguir, sobre instalação, compilação e execução foram executadas em máquinas com Ubuntu versão 18.04.1 LTS.

- [Trabalho 1 - Construção de Compiladores 2](#trabalho-1---constru%C3%A7%C3%A3o-de-compiladores-2)
    - [Pré-requisitos](#pr%C3%A9-requisitos)
    - [Estrutura do projeto](#estrutura-do-projeto)
    - [Como compilar](#como-compilar)
    - [Como executar](#como-executar)
    - [Arquivos adicionais](#arquivos-adicionais)

## Pré-requisitos

Para compilar e executar o Compilador, é necessário possuir as seguintes ferramentas instaladas:

- [Java, versão 8 ou maior](http://openjdk.java.net/install/);
- [Apache Maven versão 3.5.2 ou maior](https://maven.apache.org/)

## Estrutura do projeto

O projeto consiste de alguns arquivos fornecidos pelo professor, como os casos de teste e o corretor automático a serem extraídos nos diretórios `casosDeTesteT1` e `CorretorTrabalho1`, respectivamente.

A implementação do projeto em si está presente no diretório CompiladorLA, seguindo a estrutura básica de um projeto Maven. As informações sobre o projeto estão indicadas no arquivo `pom.xml`. A pasta `src/antlr4` contém a gramática da linguagem no formato Antlr4 e  a pasta `src/java` contém os códigos-fonte Java usados para implementar o compilador.

Os arquivos executáveis serão gerados na pasta `target`.

A estrutura de pastas e arquivos esperados estão indicados abaixo:

```
Trabalho1
│
│   testar_arquivo.sh
│   testar_professor.sh
│   README.md
│
└───casosDeTesteT1
│
└───CompiladorLA
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
└───CorretorTrabalho1
```

## Como compilar

Para compilar o projeto Maven, basta executar, dentro do diretório `CompiladorLA`, o seguinte comando:

```
mvn clean package
```

Um arquivo `CompiladorLA-1.0-SNAPSHOT-jar-with-dependencies.jar` será gerado no diretório `target`, como indicado acima.

## Como executar

Para executar o compilador, deve-se executar o seguinte comando:

```
java -jar CompiladorLA/target/CompiladorLA-1.0-SNAPSHOT-jar-with-dependencies.jar
```

A execução espera alguns parâmetros:

- Argumento 1: Arquivo a ser compilado (obrigatório);
- Argumento 2: Arquivo de saída (opcional)

A execução da ferramenta de correção automática do projeto deve seguir as instruções indicadas pelo professor no documento disponibilizado.

## Arquivos adicionais

Alguns arquivos _bash_ foram implementados para facilitar as tarefas de compilação e execução do projeto.

O _script_ `testar_arquivo.sh`, recebe um parâmetro `-f`, correspondente ao caminho do arquivo a ser testado, a saída será escrita na tela.

O _script_ `testar_professor.sh`, recebe um parâmetro, podendo possuir quatro valores, indicando a tarefa a ser executada pelo corretor automático:

- `-l`, análise léxica;
- `-s`, análise sintática;
- `-m`, análise semântica;
- `-t`, tudo

As saídas durante a correção serão escritas na pasta `Trabalho1/temp` e o resultado da correção será exibido na tela.

Ambos os _scripts_ devem ser executados dentro do diretório `Trabalho1` do projeto, e necessitam da estrutura de pastas e arquivos indicada anteriormente.
