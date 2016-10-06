parser grammar SchnappiParser;
options{
    language = Java;
    tokenVocab = SchnappiLexer;
}
solver : signature? program;
signature : SIGNATURE COLON LBRA (source) (binaryexpression SEMICOLON)* RBRA;
source : ARROBA ID EQ VTYPE SEMICOLON;
program : op*;
op : assig SEMICOLON;
assig : (VAR var | sid | binding) EQ expr;
funcs : map | spawn | join | init | sort | mappart | range | zip;

init : INIT LPAR  (literalorbinding (COMA literalorbinding)*)? RPAR;
map : MAPKW LPAR (var | string) COMA table RPAR;
mappart : MAPPART LPAR (var | string) COMA table RPAR;
join : JOIN LPAR (table (COMA table)*) RPAR;
spawn : SPAWN LPAR var COMA (INTEGER | bindingexpression) RPAR;
sort : SORT LPAR table COMA num RPAR;
range: RANGE LPAR num RPAR;
zip: ZIP LPAR (table (COMA table)*) RPAR;

expr : atomic | funcs;
atomic :  num | var | sid | string ;
atomicorbinding : atomic | bindingexpression;
literal : num | string | bool;
literalorbinding: literal | bindingexpression;
table: var | sid | bindingexpression;
binding: ARROBA(ID)(edgeexpansion)*(POINT leaf);
bindingfuncs: length;
bindingexpression: binding | bindingfuncs;
binaryexpression : literalorbinding logicoperation literalorbinding;
logicoperation : EQQ | NEQ;
length: LENGTH LPAR binding RPAR;
var: ID;
sid: SID;
num: INTEGER | FLOATING;
edgeexpansion: (arrow ID) (LCLA (binaryexpression (COMA binaryexpression)*)+ RCLA)?;
arrow: ARROWOUTGOING | ARROWINGOING;
leaf: ID;
string: STRING;
bool: TRUE | FALSE;
