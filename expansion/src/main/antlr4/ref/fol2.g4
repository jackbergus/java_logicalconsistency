grammar fol2;

expansion : (commands ';')* commands '.';

commands : BIND fol_two+ FOR LPAR expansion_opts* RPAR ;

fol_two: variable=STRING IN '{' stringlist '}';
stringlist : (VALUE ',')* VALUE ;

expansion_opts: VAR '(' VALUE ')'
              | VALUE
              | commands;

IN : 'in';
FOR : 'expand';
VAR : 'var';
BIND : 'forall';
LPAR : '[';
RPAR : ']';
LT : '<';
GT : '>';
STRING :  CHAR_NO_NL+ ;
fragment CHAR_NO_NL : 'a'..'z'|'A'..'Z';
WS
    : [ \t\r\n]+ -> channel(HIDDEN)
;

COMMENT
    : '/*' .*? '*/' -> skip
;

LINE_COMMENT
    : '#' ~[\r\n]* -> skip
;
VALUE  : '"' (~('"' | '\\' | '\r' | '\n') | '\\' ('"' | '\\'))* '"';