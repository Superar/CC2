grammar Chronological;

@header {package br.ufscar.dc.antlr;}

/*-------------------- Regras sintáticas --------------------*/

// Podem ser implementados mais de um cronograma em um unico arquivo
cronogramas: (cronograma)+;

// Cronograma e identificado pela palavra chave "Cronograma" seguido de um nome identificador. Um
// cronograma possui diversas atividades e um conjunto opcional de configuracoes globais.
cronograma:
	'Cronograma' IDENT '{' (atividades)+ (configuracao)? '}';

// As atividades sao separadas por virgula e deve ser necessaria a presenca de pelo menos uma
// atividade
atividades: 'Atividades' '{' atividade (',' atividade)* '}';

// Uma atividade possui uma descricao, as datas em que sera realizada e um conjunto opcional de
// configuracoes locais que serao aplicadas apenas as barras daquela atividade
atividade:
	'Atividade' IDENT '{' (
		descricao ',' datas (',' configuracao)? (
			',' dependencias
		)?
	) '}';

// A descricao de uma atividade e um texto delimitado por aspas duplas
descricao: TEXTO;

// Deve existir pelo menos uma data para a atividade
datas: 'Datas' '{' (periodo)+ '}';

// Um periodo possui a indicacao de inicio e fim da atividade
periodo: dataInicio = DATA '-' dataFinal = DATA;

// As configuracoes sao separadas por virgula e deve ser necessaria a presenca de pelo menos uma
// configuracao
configuracao:
	'Configuracao' '{' config1 = configs (
		',' listaConfig += configs
	)* '}';

// Configuracoes disponiveis
configs:
	'Linhas horizontais'
	| 'Linhas verticais'
	| cor
	| altura_barra
	| 'Formato: ' data_formato
	| 'Mostrar dias';

// Cor pode ser implementada como RGB ou como formato hexadecial
cor: 'Cor: ' (cor_rgb | cor_hex);

cor_rgb:
	'(' NUMERO_INTEIRO ',' NUMERO_INTEIRO ',' NUMERO_INTEIRO ')';

cor_hex: NUMERO_HEX;

altura_barra: 'Altura da barra: ' NUMERO_REAL;

// A dependencia com apenas o identificador pode ser utilizada apenas no caso de a atividade de
// referencia possuir uma unica barra, caso contrario, especifica-se a barra com um numero separado
// por ponto
dependencias:
	'Depende' '{' dep1 = dependencia (
		',' listaDeps += dependencia
	)* '}';

dependencia: IDENT ('.' (NUMERO_INTEIRO))?;

// Tres tipos de data sao identificados
data_formato: LITTLE_ENDIAN | MID_ENDIAN | BIG_ENDIAN;

/*-------------------- Regras léxicas --------------------*/

fragment LETRA: [a-zA-Z];
fragment DIGITO: [0-9];
fragment DIGITO_HEX: [0-9a-fA-F];

// Identificadores iniciam com letra ou underscore
IDENT: (LETRA | '_') (LETRA | DIGITO | '_')*;

// Textos sao delimitados por aspas duplas
TEXTO: '"' ~('"')* '"';

fragment DIA_MES: DIGITO DIGITO;
fragment ANO: DIGITO DIGITO DIGITO DIGITO;

DATA:
	DIA_MES SEPARADORES_DATA DIA_MES SEPARADORES_DATA ANO // little/mid-endian
	| ANO SEPARADORES_DATA DIA_MES SEPARADORES_DATA DIA_MES; // big-endian

// Numero hexadecimal deve comecar com 0x para diferenciar de identificadores ou outros numeros
NUMERO_HEX: '0x' (DIGITO_HEX)+;
NUMERO_INTEIRO: (DIGITO)+;
NUMERO_REAL: (DIGITO)+ ('.' (DIGITO)+)?;

// Configuracao de datas
fragment DIA_CONFIG: 'dd';
fragment MES_CONFIG: 'mm';
fragment ANO_CONFIG: 'yyyy';
// Separadores das datas podem ser: / - ou .
SEPARADORES_DATA: [/\-.];

// Formatos de data
LITTLE_ENDIAN:
	DIA_CONFIG sep1 = SEPARADORES_DATA MES_CONFIG sep2 = SEPARADORES_DATA ANO_CONFIG; // dd-mm-yyyy
MID_ENDIAN:
	MES_CONFIG sep1 = SEPARADORES_DATA DIA_CONFIG sep2 = SEPARADORES_DATA ANO_CONFIG; // mm-dd-yyyy
BIG_ENDIAN:
	ANO_CONFIG sep1 = SEPARADORES_DATA MES_CONFIG sep2 = SEPARADORES_DATA DIA_CONFIG; // yyyy-mm-dd

// Ignora espacos em branco
WS: (' ' | '\t' | '\r' | '\n') {skip();} -> skip;