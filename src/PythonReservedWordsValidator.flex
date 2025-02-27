/* JFlex specification for Python reserved words validator */
%%

%class PythonReservedWordsValidator
%unicode
%public
%type String

%{
    // Returns "valid" for reserved words, "invalid" for anything else
    private String result(String status) {
        return status;
    }
%}

/* Basic definitions */
WhiteSpace = [ \t\f\r\n]
InputCharacter = [^\r\n]
Identifier = [a-zA-Z_][a-zA-Z0-9_]*

%%

/* Python reserved keywords */
"and"       { return result("valid"); }
"as"        { return result("valid"); }
"assert"    { return result("valid"); }
"break"     { return result("valid"); }
"class"     { return result("valid"); }
"continue"  { return result("valid"); }
"def"       { return result("valid"); }
"del"       { return result("valid"); }
"elif"      { return result("valid"); }
"else"      { return result("valid"); }
"except"    { return result("valid"); }
"False"     { return result("valid"); }
"finally"   { return result("valid"); }
"for"       { return result("valid"); }
"from"      { return result("valid"); }
"global"    { return result("valid"); }
"if"        { return result("valid"); }
"import"    { return result("valid"); }
"in"        { return result("valid"); }
"is"        { return result("valid"); }
"lambda"    { return result("valid"); }
"None"      { return result("valid"); }
"nonlocal"  { return result("valid"); }
"not"       { return result("valid"); }
"or"        { return result("valid"); }
"pass"      { return result("valid"); }
"raise"     { return result("valid"); }
"return"    { return result("valid"); }
"True"      { return result("valid"); }
"try"       { return result("valid"); }
"while"     { return result("valid"); }
"with"      { return result("valid"); }
"yield"     { return result("valid"); }

/* Whitespace - ignore */
{WhiteSpace}    { /* Ignore */ }

/* Everything else is not a reserved word */
{Identifier}    { return result("invalid"); }
[^]             { return result("invalid"); }
