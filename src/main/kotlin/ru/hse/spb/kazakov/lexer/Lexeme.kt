package ru.hse.spb.kazakov.lexer

import kotlin.math.pow


interface Lexeme {
    val length: Int
}

enum class Operator(override val length: Int, private val function: (Int, Int) -> Int) : Lexeme {
    ADDITION(1, { x, y -> x + y }),
    SUBTRACTION(1, { x, y -> x - y }),
    MULTIPLICATION(1, { x, y -> x * y }),
    DIVISION(1, { x, y -> x / y }),
    MODULO(1, { x, y -> x % y }),
    EXPONENTIATION(1, { x, y -> x.toDouble().pow(y).toInt() }),
    LESS(1, { x, y -> if (x < y) 1 else 0 }),
    GREATER(1, { x, y -> if (x > y) 1 else 0 }),
    LESS_OR_EQ(2, { x, y -> if (x <= y) 1 else 0 }),
    GREATER_OR_EQ(2, { x, y -> if (x >= y) 1 else 0 }),
    EQUAL(2, { x, y -> if (x == y) 1 else 0 }),
    NOT_EQUAL(2, { x, y -> if (x != y) 1 else 0 }),
    AND(2, { x, y -> if (x != 0 && y != 0) 1 else 0 }),
    OR(2, { x, y -> if (x != 0 || y != 0) 1 else 0 }),
    ASSIGN(1, { _, y -> y});

    fun evaluate(x: Int, y: Int) = function(x, y)
}

enum class Keyword : Lexeme {
    IF, ELSE, WHILE, FUN, VAR, RETURN;

    override val length: Int
        get() = toString().length
}

enum class Separator : Lexeme {
    COMMA, LEFT_BRACKET, RIGHT_BRACKET, LEFT_BRACE, RIGHT_BRACE, LF, EOF;

    override val length: Int
        get() = 1
}

data class Identifier(val value: String) : Lexeme {
    override val length: Int
        get() = value.length
}

data class Literal(val value: Int) : Lexeme {
    override val length: Int
        get() = value.toString().length
}

data class PosInFile(val line: Int, val begin: Int) {
    override fun toString(): String {
        return "($line, $begin)"
    }
}

data class LexemePosition(
        val lexeme: Lexeme,
        val line: Int,
        val begin: Int,
        val end: Int
) {
    fun getPosition(): PosInFile = PosInFile(line, begin)
}