/* JFlex specification for Python lexical analyzer */
%%

%class PythonLexer
%unicode
%line
%column
%type Token

%{
    public String lexeme;

    private Token token(String tokenType) {
        return new Token(tokenType, yytext(), yyline + 1, yycolumn + 1);
    }
%}

/* Whitespace */
LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]
InputCharacter = [^\r\n]

/* Comments */
Comment = "#" {InputCharacter}* {LineTerminator}?

/* Identifiers and Keywords */
Letter = [a-zA-Z_]
Digit = [0-9]
Identifier = {Letter}({Letter}|{Digit})*
InvalidIdentifier = {Digit}+{Letter}+({Letter}|{Digit})*

/* Potential Keywords - any word that might be trying to be a keyword */
PotentialKeyword = [a-z]+

/* Numbers */
Integer = {Digit}+
Float = {Digit}+"."{Digit}* | "."{Digit}+
Scientific = ({Integer}|{Float})[eE][+-]?{Integer}

/* String literals */
StringCharacter = [^\r\n\"\'\\]
SingleString = \'{StringCharacter}*\'
DoubleString = \"{StringCharacter}*\"
String = {SingleString}|{DoubleString}

%%

/* Keywords */
"and"           { return token("AND"); }
"as"            { return token("AS"); }
"assert"        { return token("ASSERT"); }
"break"         { return token("BREAK"); }
"class"         { return token("CLASS"); }
"continue"      { return token("CONTINUE"); }
"def"           { return token("DEF"); }
"del"           { return token("DEL"); }
"elif"          { return token("ELIF"); }
"else"          { return token("ELSE"); }
"except"        { return token("EXCEPT"); }
"False"         { return token("FALSE"); }
"finally"       { return token("FINALLY"); }
"for"           { return token("FOR"); }
"from"          { return token("FROM"); }
"global"        { return token("GLOBAL"); }
"if"            { return token("IF"); }
"import"        { return token("IMPORT"); }
"in"            { return token("IN"); }
"is"            { return token("IS"); }
"lambda"        { return token("LAMBDA"); }
"None"          { return token("NONE"); }
"nonlocal"      { return token("NONLOCAL"); }
"not"           { return token("NOT"); }
"or"            { return token("OR"); }
"pass"          { return token("PASS"); }
"raise"         { return token("RAISE"); }
"return"        { return token("RETURN"); }
"True"          { return token("TRUE"); }
"try"           { return token("TRY"); }
"while"         { return token("WHILE"); }
"with"          { return token("WITH"); }
"yield"         { return token("YIELD"); }

/* Operators and punctuation */
"+"             { return token("PLUS"); }
"-"             { return token("MINUS"); }
"*"             { return token("MULTIPLY"); }
"/"             { return token("DIVIDE"); }
"//"            { return token("INTEGER_DIVIDE"); }
"%"             { return token("MODULO"); }
"**"            { return token("POWER"); }
"="             { return token("ASSIGN"); }
"=="            { return token("EQUALS"); }
"!="            { return token("NOT_EQUALS"); }
"<"             { return token("LESS"); }
">"             { return token("GREATER"); }
"<="            { return token("LESS_EQUALS"); }
">="            { return token("GREATER_EQUALS"); }
"("             { return token("LPAREN"); }
")"             { return token("RPAREN"); }
"["             { return token("LBRACKET"); }
"]"             { return token("RBRACKET"); }
"{"             { return token("LBRACE"); }
"}"             { return token("RBRACE"); }
","             { return token("COMMA"); }
"."             { return token("DOT"); }
":"             { return token("COLON"); }
";"             { return token("SEMICOLON"); }

/* Identifiers */
{Identifier}    { return token("IDENTIFIER"); }

/* Literals */
{Integer}       { return token("INTEGER_LITERAL"); }
{Float}         { return token("FLOAT_LITERAL"); }
{Scientific}    { return token("SCIENTIFIC_LITERAL"); }
{String}        { return token("STRING_LITERAL"); }

/* Comments */
{Comment}       { return token("COMMENT"); }

/* Whitespace */
{WhiteSpace}    { /* Ignore */ }

/* Invalid identifiers */
{InvalidIdentifier}    { return token("ERROR"); }

/* Potential Keywords - check if it's a valid keyword, otherwise it's an error */
{PotentialKeyword} {
    String text = yytext();
    switch(text) {
        case "and": case "as": case "assert": case "break": case "class":
        case "continue": case "def": case "del": case "elif": case "else":
        case "except": case "false": case "finally": case "for": case "from":
        case "global": case "if": case "import": case "in": case "is":
        case "lambda": case "none": case "nonlocal": case "not": case "or":
        case "pass": case "raise": case "return": case "true": case "try":
        case "while": case "with": case "yield":
            return token(text.toUpperCase());
        default:
            return token("ERROR");
    }
}

/* Valid identifier (must come after keyword checks) */
{Identifier}          { return token("IDENTIFIER"); }

/* Error fallback - catch any other character */
[^]                   { return token("ERROR"); }
