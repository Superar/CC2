grammar gramaticaLA;

/*-------------------- Regras sintáticas --------------------*/

programa: declaracoes 'algoritmo' corpo 'fim_algoritmo';
declaracoes: (decl_local_global)*;
decl_local_global: declaracao_local | declaracao_global;
declaracao_local:
	'declare' variavel
	| 'constatnte' IDENT ':' tipo_basico '=' valor_constante
	| 'tipo' IDENT ':' tipo;
variavel: identificador (',' identificador)* ':' tipo;
identificador: IDENT ('.' IDENT)* dimensao;
dimensao: ('[' exp_aritmetica ']')*;
tipo: registro | tipo_estentido;
tipo_basico: 'literal' | 'inteiro' | 'real' | 'logico';
tipo_basico_ident: tipo_basico | IDENT;
tipo_estentido: ('^')? tipo_basico_ident;
valor_constante:
	CADEIA
	| NUM_INT
	| NUM_REAL
	| 'verdadeiro'
	| 'falso';
registro: 'registro' (variavel)* 'fim_registro';
declaracao_global:
	'procedimento' IDENT '(' (parametros)? ')' (declaracao_local)* (
		cmd
	)* 'fim_procedimento'
	| 'funcao' IDENT '(' (parametros)? ')' ':' tipo_estentido (
		declaracao_local
	)* (cmd)* 'fim_funcao';
parametro: ('var')? identificador (',' identificador)* ':' tipo_estentido;
parametros: parametro (',' parametro)*;
corpo: (declaracao_local)* (cmd)*;
cmd:
	cmdLeia
	| cmdEscreva
	| cmdSe
	| cmdCaso
	| cmdPara
	| cmdEnquanto
	| cmdFaca
	| cmdAtribuicao
	| cmdChamada
	| cmdRetorne;
cmdLeia:
	'leia' '(' ('^')? identificador (',' ('^')? identificador)* ')';
cmdEscreva: 'escreva' '(' expressao (',' expressao)* ')';
cmdSe: 'se' expressao 'entao' (cmd)* ('senao' (cmd)*)? 'fim_se';
cmdCaso:
	'caso' exp_aritmetica 'seja' selecao ('senao' (cmd)*)? 'fim_caso';
cmdPara:
	'para' IDENT '<-' exp_aritmetica 'ate' exp_aritmetica 'faca' (
		cmd
	)* 'fim_para';
cmdEnquanto: 'enquanto' expressao 'faca' (cmd)* 'fim_enquanto';
cmdFaca: 'faca' (cmd)* 'ate' expressao;
cmdAtribuicao: ('^')? identificador '<-' expressao;
cmdChamada: IDENT '(' expressao (',' expressao)* ')';
cmdRetorne: 'retorne' expressao;
selecao: (item_selecao)*;
item_selecao: constatntes ':' (cmd)*;
constatntes: numero_intervalo (',' numero_intervalo)*;
numero_intervalo: (op_unario)? NUM_INT (
		'..' (op_unario)? NUM_INT
	)?;
op_unario: '-';
exp_aritmetica: termo (op1 termo)*;
termo: fator (op2 fator)*;
fator: parcela (op3 parcela)*;
op1: '+' | '-';
op2: '*' | '/';
op3: '%';
parcela: (op_unario)? parcela_unario | parcela_nao_unario;
parcela_unario: ('^')? identificador
	| IDENT '(' expressao (',' expressao)* ')'
	| NUM_INT
	| NUM_REAL
	| '(' expressao ')';
parcela_nao_unario: '&' identificador | CADEIA;
exp_relacional: exp_aritmetica (op_relacional exp_aritmetica)?;
op_relacional: '=' | '<>' | '>=' | '<=' | '>' | '<';
expressao: termo_logico (op_logico_1 termo_logico)*;
termo_logico: fator_logico (op_logico_2 fator_logico);
fator_logico: ('nao')? parcela_logica;
parcela_logica: ('verdadeiro' | 'falso') | exp_relacional;
op_logico_1: 'ou';
op_logico_2: 'e';

/*-------------------- Regras léxicas --------------------*/

fragment LETRA: ('a' ..'z' | 'A' ..'Z');
fragment DIGITO: ('0' ..'9');

// Identificador segue duas regras: É formado apenas por letras do alfabeto (a a z), por dígitos (0
// a 9) e pela sublinha (_) e o primeiro caractere é sempre uma letra ou a sublinha, nunca um
// dígito.
IDENT: (LETRA | '_') (LETRA | DIGITO | '_')*;

// Literais sao delimitados por aspas duplas ("") e podem ser vazios
CADEIA: '"' ~('\n' | '\r' | '"')* '"';

// Numeros inteiros possuem um ou mais dígitos
NUM_INT: (DIGITO)+;
// Numeros reais possuem as casas decimais separadas por vírgula
NUM_REAL: (DIGITO)+ ',' (DIGITO)+;

// Ignora comentarios e espacos em branco
COMENTARIO: '{' .*? '}' {skip();} -> skip;
WS: (' ' | '\t' | '\r' | '\n') {skip();} -> skip;