grammar chronological;

cronogramas: (cronograma)+;

cronograma:
	'Cronograma' IDENT '{' (atividades)* (configuracao)? '}';

atividades: 'Atividades' '{' atividade (',' atividade)* '}';

atividade:
	'Atividade' IDENT '{' (
		descricao ',' datas (',' configuracao)?
	) '}';

descricao: TEXTO;

datas: 'Datas' '{' (periodo)+ '}';

periodo: DATA '-' DATA;

configuracao: 'Configuracao' '{' configs (',' configs)* '}';

configs:
	'Linhas horizontais'
	| 'Linhas verticais'
	| cor
	| altura_barra
	| 'Formato: ' DATA_FORMATO;

cor: 'Cor: ' (cor_rgb | cor_hex);

cor_rgb:
	'(' NUMERO_INTEIRO ',' NUMERO_INTEIRO ',' NUMERO_INTEIRO ')';

cor_hex: NUMERO_HEX;

altura_barra: 'Altura da barra: ' NUMERO_REAL;

fragment LETRA: [a-zA-Z];
fragment DIGITO: [0-9];
fragment DIGITO_HEX: (DIGITO | [a-f] | [A-F]);
fragment DIA: 'dd';
fragment MES: 'mm';
fragment ANO: 'yyyy';
fragment SEPARADORES_DATA: [/\\\-.];

IDENT: (LETRA | '_') (LETRA | DIGITO | '_')*;

TEXTO: '"' ~('"')* '"';

DATA: DIGITO+ SEPARADORES_DATA DIGITO+ SEPARADORES_DATA DIGITO+;

DATA_FORMATO: (
		DIA SEPARADORES_DATA MES SEPARADORES_DATA ANO
	) // little-endian
	| (
		MES SEPARADORES_DATA DIA SEPARADORES_DATA ANO
	) // middle-endian
	| (ANO SEPARADORES_DATA MES SEPARADORES_DATA DIA); // big-endian

NUMERO_INTEIRO: (DIGITO)+;

NUMERO_HEX: '0x' (DIGITO_HEX)+;

NUMERO_REAL: (DIGITO)+ ('.' (DIGITO)+)?;

WS: (' ' | '\t' | '\r' | '\n') {skip();} -> skip;