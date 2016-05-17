parser grammar SchnappiParser;
options{
    language = Java;
    tokenVocab = SchnappiLexer;
}
program : op*;
op   : map | reduce;
map : MAPKW LPAR ID COMA ID RPAR;
reduce : REDUCEKW LPAR ID COMA ID RPAR;
