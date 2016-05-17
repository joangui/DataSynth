lexer grammar SchnappiLexer;
LPAR : '(';
RPAR : ')';
COMA : ',';
MAPKW : 'map';
REDUCEKW : 'reduce';
ID  : [a-z]+ ;
WS  : [ \t\r\n]+ -> skip ;