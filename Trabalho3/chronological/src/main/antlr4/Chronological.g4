grammar Chronological;

@header {package br.ufscar.dc.antlr;}

/*-------------------- Regras sintáticas --------------------*/

// Podem ser implementados mais de um cronograma em um unico arquivo
cronogramas: (cronograma)+;

// Cronograma e identificado pela palavra chave "Cronograma" seguido de um nome identificador. Um
// cronograma possui diversas atividades e um conjunto opcional de configuracoes globais.
cronograma:
	'Cronograma' IDENT '{' (atividades)* (configuracao)? '}';

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
periodo: DATA '-' DATA;

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
	| 'Formato: ' DATA_FORMATO
	| 'Mostrar dias';

// Cor pode ser implementada como RGB ou como formato hexadecial
cor: 'Cor: ' (cor_rgb | cor_hex);

cor_rgb:
	'(' NUMERO_INTEIRO ',' NUMERO_INTEIRO ',' NUMERO_INTEIRO ')';

cor_hex: NUMERO_HEX;

altura_barra: 'Altura da barra: ' NUMERO_REAL;

dependencias: 'Depende' '{' (IDENT)+ '}';

/*-------------------- Regras léxicas --------------------*/

fragment LETRA: [a-zA-Z];
fragment DIGITO: [0-9];
fragment DIGITO_HEX: [0-9a-fA-F];
fragment DIA: 'dd';
fragment MES: 'mm';
fragment ANO: 'yyyy';
// Separadores das datas podem ser: / - ou .
fragment SEPARADORES_DATA: [/\-.];

// Identificadores iniciam com letra ou underscore
IDENT: (LETRA | '_') (LETRA | DIGITO | '_')*;

// Textos sao delimitados por aspas duplas
TEXTO: '"' ~('"')* '"';

DATA:
	DIGITO DIGITO SEPARADORES_DATA DIGITO DIGITO SEPARADORES_DATA DIGITO DIGITO DIGITO DIGITO
	| DIGITO DIGITO DIGITO DIGITO SEPARADORES_DATA DIGITO DIGITO SEPARADORES_DATA DIGITO DIGITO;

// Tres tipos de data sao identificados
DATA_FORMATO:
	DIA SEPARADORES_DATA MES SEPARADORES_DATA ANO // little-endian (dd-mm-yyyy)
	| MES SEPARADORES_DATA DIA SEPARADORES_DATA ANO // middle-endian (mm-dd-yyyy)
	| ANO SEPARADORES_DATA MES SEPARADORES_DATA DIA; // big-endian (yyyy-mm-dd)

NUMERO_INTEIRO: (DIGITO)+;

// Numero hexadecimal deve comecar com 0x para diferenciar de identificadores ou outros numeros
NUMERO_HEX: '0x' (DIGITO_HEX)+;

NUMERO_REAL: (DIGITO)+ ('.' (DIGITO)+)?;

// Ignora espacos em branco
WS: (' ' | '\t' | '\r' | '\n') {skip();} -> skip;