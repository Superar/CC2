grammar chronological;

cronogramas: (cronograma)+;

cronograma:
	'Cronograma' IDENT '{' (atividades)* ',' (datas)* (
		',' configuracao
	)? '}';

atividades: 'Atividades' '{' atividade (',' atividade)* '}';

datas: 'Datas' '{' atividade (',' atividade)* '}';

atividade: 'Atividade' IDENT '{' (descricao | data) '}';

descricao: TEXTO;

data: 'Inicio:' DATA ',' 'Fim:' DATA;

configuracao: 'Configuracao' '{' configs (',' configs)* '}';

configs: 'Linhas horizontais' | 'Linhas verticais' | cor | altura_barra;

cor: 'Cor: ' (cor_rgb | cor_hex);

cor_rgb: '(' NUMERO_INTEIRO ',' NUMERO_INTEIRO ',' NUMERO_INTEIRO ')';

cor_hex: NUMERO_HEX;

altura_barra: 'Altura da barra: ' NUMERO_REAL;

fragment LETRA: ('a' ..'z' | 'A' ..'Z');
fragment DIGITO: ('0' ..'9');
fragment DIGITO_HEX: (DIGITO | 'a' ..'f' | 'A' ..'F');

IDENT: (LETRA | '_') (LETRA | DIGITO | '_')*;

TEXTO: '"' ~('"')* '"';

DATA: DIGITO+ '/' DIGITO+ '/' DIGITO+;

NUMERO_INTEIRO: (DIGITO)+;

NUMERO_HEX: (DIGITO_HEX)+;

NUMERO_REAL: (DIGITO)+ '.' (DIGITO)+;

WS: (' ' | '\t' | '\r' | '\n') {skip();} -> skip;