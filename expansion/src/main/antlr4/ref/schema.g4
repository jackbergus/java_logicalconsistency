grammar schema;

program : (commands ';')* commands '.';

commands : (RELATION|EVENT) STRING LPAR stringlist RPAR                            #rel_delcare
         | FD FOR STRING DECLARE stringlist MARROW stringlist                      #fdep_declare
         | RULE moreClauses (WITH predicates+)? IMPLIES (moreClauses | BOT)        #rule
         | ENTITY EXIST (stringlist)? except? intime inspace?                      #enexists
         | MACRO STRING LPAR stringlist RPAR DECLARE STRING COMMA
           RULE moreClauses (WITH predicates+)? IMPLIES (moreClauses | BOT)        #macro_definition
         | TRYEXPAND stringlist (FOR (RELATION|EVENT))?                            #macro_expand
         | UNIQUE? (BEGIN|END) STRING LPAR orig=stringlist RPAR intime             #beginend_declare
         | TRANSFER STRING LPAR orig=stringlist RPAR intime DECLARE dest=stringlist  #transfer_macro
         ;

except : EXCEPT stringlist;
intime : 'in time' stringlist;
inspace : 'and space' stringlist;

setOptions : RELATION (STRING LPAR STRING COMMA STRING RPAR)? #set_relation
           | EVENT    (STRING LPAR stringlist RPAR)?          #set_event
           | ENTITY   STRING                                  #set_entity
           ;

paramDeclared : FOR STRING; 

stringlist : (angestrengend COMMA)* angestrengend;

moreClauses : clause+;
clause : forall* exists* NEG? STRING LPAR stringlist RPAR
       ;

forall : '\\forall' STRING TYPE STRING (WITH predicates+)? '.';
exists : '\\exists' STRING TYPE STRING (WITH predicates+)? '.';

predicates : STRING 'notnull'               #var_notnull
           | lbound? STRING ubound?          #quantification
           ;

angestrengend : STRING #isstring
              | VALUE  #isvalue
              ;

lbound : STRING (LT | LEQ) ;
ubound : (GT | GEQ) STRING ;

TRANSFER : 'transfer';
TRYEXPAND : 'try-expand';
RELATION : 'relation' ;
FUTURECHECK : 'future-check';
EVENT : 'event' ;
UNIQUE : 'unique';
ENTITY : 'entity';
FD : 'MVD';
RULE : 'rule';
FOR : 'for';
DECLARE : 'as';
SET : 'set';
WITH : 'with';
BEGIN : 'begin';
END : 'end';
EXIST : 'exist';
EXCEPT : 'except';
MACRO : 'macro';
MARROW : '->>';
IMPLIES : '=>';
LPAR : '(';
RPAR : ')';
LT : '<';
GT : '>';
LEQ : '<=';
GEQ : '>=';
COMMA : ',';
TYPE : ':';
NEG : '~';
BOT : 'False';
VALUE  : '"' STRING '"';
STRING :  CHAR_NO_NL+ ;
fragment CHAR_NO_NL : 'a'..'z'|'A'..'Z'|'.';
WS
    : [ \t\r\n]+ -> channel(HIDDEN)
;

COMMENT
    : '/*' .*? '*/' -> skip
;

LINE_COMMENT
    : '#' ~[\r\n]* -> skip
;