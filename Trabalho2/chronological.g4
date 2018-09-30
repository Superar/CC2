grammar chronological;

cronogramas: (cronograma)+;

cronograma: 'Cronograma' IDENT '{' (atividades)* ',' (datas)* '}';

atividades: 'Atividades' '{' atividade (',' atividade)* '}';

datas: 'Datas' '{' atividade (',' atividade)* '}';

atividade: 'Atividade' IDENT '{' (descricao | data) '}';

descricao: TEXTO;

data: 'Inicio:' DATA ',' 'Fim:' DATA;

fragment LETRA: ('a' ..'z' | 'A' ..'Z');
fragment DIGITO: ('0' ..'9');

IDENT: (LETRA | '_') (LETRA | DIGITO | '_')*;

TEXTO: '"' ~('"')* '"';

DATA: DIGITO+ '/' DIGITO+ '/' DIGITO+;

WS: (' ' | '\t' | '\r' | '\n') {skip();} -> skip;