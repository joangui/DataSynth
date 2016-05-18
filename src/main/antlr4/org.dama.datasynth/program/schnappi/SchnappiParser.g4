parser grammar SchnappiParser;
options{
    language = Java;
    tokenVocab = SchnappiLexer;
}
program : op*;
op   : assig | init ;
init : INIT LPAR ID RPAR;
funcs : map | reduce | join | genids;
assig : ID EQ expr;
map : MAPKW LPAR ID COMA ID RPAR;
reduce : REDUCEKW LPAR ID COMA ID RPAR;
join : JOIN LPAR ID COMA ID RPAR;
genids : GENID LPAR NUM RPAR;
expr : atom;
atom : NUM | ID | funcs;