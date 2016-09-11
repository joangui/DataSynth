parser grammar SchnappiParser;
options{
    language = Java;
    tokenVocab = SchnappiLexer;
}
solver : signature? program;
signature : SIGNATURE COLON LBRA source target RBRA;
source : ARROBA ID EQ VTYPE;
target : ARROBA ID EQ VTYPE;
program : op*;
op : assig SEMICOLON;
init : NEW (binding | ID ) LPAR params RPAR;
funcs : map | reduce | eqjoin | genids | union | init | sort | partition | mappart | filter;
assig : (ID | binding) EQ expr;
map : MAPKW LPAR atomic COMA atomic RPAR;
mappart : MAPPART LPAR atomic COMA atomic RPAR;
reduce : REDUCEKW LPAR atomic COMA atomic RPAR;
eqjoin : EQJOIN LPAR params RPAR;
union : UNION LPAR (params)? RPAR;
genids : GENID LPAR NUM RPAR;
sort : SORT LPAR params RPAR;
partition : PART LPAR params RPAR;
filter : FILTER LPAR atomic COMA set RPAR;
expr : atomic | funcs;
params : (atomic (COMA atomic)*);
atomic :  NUM | binding | ID | STRING ;
set : LBRA NUM (COMA NUM)* RBRA;
binding: ARROBA(ID)(ARROW ID)*(POINT ID);
