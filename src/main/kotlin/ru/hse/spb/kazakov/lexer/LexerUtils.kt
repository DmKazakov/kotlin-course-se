package ru.hse.spb.kazakov.lexer


fun String.toLexeme(): Lexeme? = when (this) {
    "+" -> Operator.ADDITION
    "-" -> Operator.SUBTRACTION
    "*" -> Operator.MULTIPLICATION
    "/" -> Operator.DIVISION
    "%" -> Operator.MODULO
    "^" -> Operator.EXPONENTIATION
    "&&" -> Operator.AND
    "||" -> Operator.OR
    ">" -> Operator.GREATER
    ">=" -> Operator.GREATER_OR_EQ
    "<" -> Operator.LESS
    "<=" -> Operator.LESS_OR_EQ
    "==" -> Operator.EQUAL
    "!=" -> Operator.NOT_EQUAL
    "=" -> Operator.ASSIGN
    "if" -> Keyword.IF
    "else" -> Keyword.ELSE
    "while" -> Keyword.WHILE
    "fun" -> Keyword.FUN
    "var" -> Keyword.VAR
    "return" -> Keyword.RETURN
    "(" -> Separator.LEFT_BRACKET
    ")" -> Separator.RIGHT_BRACKET
    "{" -> Separator.LEFT_BRACE
    "}" -> Separator.RIGHT_BRACE
    "\n" -> Separator.LF
    "," -> Separator.COMMA
    else -> null
}

fun Char.toLexeme(): Lexeme? = this.toString().toLexeme()

fun Char.isIdentifierStart(): Boolean = isLetter() || this == '_'

fun Char.isIdentifierPart(): Boolean = isIdentifierStart() || isDigit()

fun Char.isSpace(): Boolean = this == ' ' || this == '\t'

fun Char.isOperatorStart(): Boolean = this == '+' || this == '-' || this == '*' ||
        this == '/' || this == '%' || this == '^' || this == '>' || this == '<' ||
        this == '=' || this == '!' || this == '&' || this == '|'

fun Char.isNumStart(): Boolean = isDigit() && this != '0'
