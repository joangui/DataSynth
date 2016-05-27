parser grammar SchnappiParser;
options{
    language = Java;
    tokenVocab = SchnappiLexer;
}
solver : signature bindings program;
bindings : BINDINGS COLON LBRA bind* RBRA;
bind : (bindhead EQ ID) | (ID EQ bindhead);
bindhead : (SOURCE | TARGET) POINT ID;
signature : SIGNATURE COLON LBRA source target RBRA;
source : SOURCE EQ VTYPE;
target : TARGET EQ VTYPE;
program : PROGRAM COLON LBRA op* RBRA;
op   : assig;
init : INIT ID LPAR params RPAR;
funcs : map | reduce | eqjoin | genids | union | init;
assig : ID EQ expr;
map : MAPKW LPAR ID COMA ID RPAR;
reduce : REDUCEKW LPAR ID COMA ID RPAR;
eqjoin : EQJOIN LPAR params RPAR;
union : UNION LPAR params RPAR;
genids : GENID LPAR NUM RPAR;
expr : atom;
atom : NUM | ID | funcs;
params : ID (COMA ID)*;