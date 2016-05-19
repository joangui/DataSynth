parser grammar SchnappiParser;
options{
    language = Java;
    tokenVocab = SchnappiLexer;
}
program : op*;
op   : assig | init ;
init : INIT LPAR ID RPAR;
funcs : map | reduce | eqjoin | genids | union;
assig : ID EQ expr;
map : MAPKW LPAR ID COMA ID RPAR;
reduce : REDUCEKW LPAR ID COMA ID RPAR;
eqjoin : EQJOIN LPAR params RPAR;
union : UNION LPAR params RPAR;
genids : GENID LPAR NUM RPAR;
expr : atom;
atom : NUM | ID | funcs;
params : ID (COMA ID)*;