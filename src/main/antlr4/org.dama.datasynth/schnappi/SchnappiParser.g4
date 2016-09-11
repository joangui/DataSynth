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
map : MAPKW LPAR atomic COMA atomic RPAR;
mappart : MAPPART LPAR atomic COMA atomic RPAR;
reduce : REDUCEKW LPAR atomic COMA atomic RPAR;
eqjoin : EQJOIN LPAR params RPAR;
union : UNION LPAR (params)? RPAR;
genids : GENID LPAR NUM RPAR;
sort : SORT LPAR params RPAR;
partition : PART LPAR params RPAR;
expr : atomic | funcs;
params : (atomic (COMA atomic)*);
atomic :  NUM | BINDING | ID | STRING ;
