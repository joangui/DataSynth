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

LCLA
    : '['
    ;

RCLA
    : ']'
    ;

TRUE
    : 'true'
    ;

FALSE
    : 'false'
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

JOIN
    : 'join'
    ;

SPAWN
    : 'spawn'
    ;

SORT
    : 'sort'
    ;

RANGE
    : 'range'
    ;

ZIP
    : 'zip'
    ;

FILTER
    : 'filter'
    ;
LENGTH
    : 'length'
    ;

DUMP
    : 'dump'
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

ARROWOUTGOING
    : '->'
    ;

ARROWINGOING
    : '<-'
    ;

ARROBA
    : '@'
    ;
HTAG
    : '#'
    ;

VAR
    : 'var'
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

SID
: '#'(ALPHA)+ (POINT ALPHA+)
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

FLOATING
    : DIGIT+ POINT DIGIT+
    ;

INTEGER
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

WS
: ('\t' | '\r' | '\n' | ' ')+ -> skip ;

COMMENT
    : '/*' .*? '*/' -> skip
;

LINE_COMMENT
    : '//' ~[\r\n]* -> skip
;