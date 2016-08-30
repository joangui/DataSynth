parser grammar SchnappiParser;
options{
    language = Java;
    tokenVocab = SchnappiLexer;
}
solver : signature program;
signature : SIGNATURE COLON LBRA source target RBRA;
source : SOURCE EQ VTYPE;
target : TARGET EQ VTYPE;
program : op*;
op : assig SEMICOLON;
init : NEW (BINDING | ID ) LPAR params RPAR;
funcs : map | reduce | eqjoin | genids | union | init | sort | partition | mappart;
assig : (ID | BINDING) EQ expr;
map : MAPKW LPAR any COMA any RPAR;
mappart : MAPPART LPAR any COMA any RPAR;
reduce : REDUCEKW LPAR any COMA any RPAR;
eqjoin : EQJOIN LPAR params RPAR;
union : UNION LPAR (params)? RPAR;
genids : GENID LPAR NUM RPAR;
sort : SORT LPAR params RPAR;
partition : PART LPAR params RPAR;
expr : any | funcs;
params : (any (COMA any)*);
any :  NUM | BINDING | ID | STRING ;
