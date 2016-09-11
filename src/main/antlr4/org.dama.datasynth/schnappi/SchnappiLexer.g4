lexer grammar SchnappiLexer;

LPAR
    : '('
    ;

RPAR
    : ')'
    ;

COMA
    : ','
    ;

POINT
    : '.'
    ;

COLON
    : ':'
    ;

SEMICOLON
    : ';'
    ;

LBRA
    : '{'
    ;

RBRA
    : '}'
    ;

MAPKW
    : 'map'
    ;

REDUCEKW
    : 'reduce'
    ;

INIT
    : 'init'
    ;

EQJOIN
    : 'eqjoin'
    ;

UNION
    : 'union'
    ;

GENID
    : 'gId'
    ;
SORT
    : 'sort'
    ;
FILTER
    : 'filter'
    ;

EQQ
    : '=='
    ;

NEQ
    : '!='
    ;
EQ
    : '='
    ;

PLUS
    : '+'
    ;

MINUS
    : '-'
    ;

STAR
    : '*'
    ;

FSLASH
    : '/'
    ;

ARROW
    : '->'
    ;

ARROBA
    : '@'
    ;

VTYPE
    : ATTR | ENTITY | RELATION;

ATTR
    : 'Attribute'
    ;

ENTITY
    : 'Entity'
    ;

RELATION
    : 'Edge'
    ;

SIGNATURE
    : 'signature'
    ;
PART
    : 'part'
    ;
MAPPART
    : 'mappart'
    ;
ID
: (ALPHA)+
;

ESC
  : '\\'
    ( 'n'
    | 'r'
    | 't'
    | 'b'
    | 'f'
    | '"'
    | '\''
    | '\\'
    | (
        ('0'..'3')
        (
          ('0'..'7')
          (
            '0'..'7'
          )?
        )?
      | ('4'..'7')
        (
          ('0'..'7')
        )?
      )
    )
  ;

NUM
: DIGIT+
;

DIGIT
  : '0'..'9'
  ;


ALPHA
  :'a'..'z'
  |'A'..'Z'
  |'_'
  //For Unicode compatibility (from 0000 to 00ff)
  |'\u00C0' .. '\u00D6'
  |'\u00D8' .. '\u00F6'
  |'\u00F8' .. '\u00FF'
  ;

STRING
: '\''
    (ESC
    |~('\\'|'\'')
    )*
    '\''
  ;





LITERAL
: STRING | NUM;

WS
: ('\t' | '\r' | '\n' | ' ')+ -> skip ;