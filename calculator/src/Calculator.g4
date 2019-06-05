grammar Calculator; // rename to distinguish from Expr.g4

@header {
	package: ua.com.advertikon.calc;
}

stat:   expr NEWLINE                # printExpr
    |   ID '=' expr NEWLINE         # assign
    |   NEWLINE                     # blank
    ;

expr:   expr PERSENT expr			# percent
    |   expr op=('*'|'/') expr      # MulDiv
    |   expr op=('+'|'-') expr      # AddSub
    |   INT                         # int
    |   ID                          # id
    |   FLOAT						# float
    |   '(' expr ')'                # parens
    |   SIN expr                    # sin
    ;

MUL       : '*' ; 			// assigns token name to '*' used above in grammar
DIV       : '/' ;
ADD       : '+' ;
SUB       : '-' ;
PERSENT   : '%' ;
SIN       : 'sin' ;
COS       : 'cos' ;
TAN       : 'tan' ;
ASIN      : 'asin' ;
ACOS      : 'acos' ;
ATAN      : 'atan' ;
LN        : 'ln' ;
LOG       : 'log' ;
POW       : '^' ;
PI        : 'pi' ;
E         : 'e' ;
SQRT      : 'sqrt' ;				// square root
FACTORIAL : '!' ;
ID        : [a-zA-Z]+ ;				// match identifiers
FLOAT     : [0-9]* '.' [0-9]+ ;		// match float
INT       : [0-9]+ ;				// match integers
NEWLINE   : '\r'? '\n' ;			// return newlines to parser (is end-statement signal)
WS        : [ \t]+ -> skip ;		// toss out whitespace