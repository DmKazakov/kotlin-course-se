package ru.hse.spb.kazakov.parser

import ru.hse.spb.kazakov.lexer.Operator

enum class OperatorType(val dec: OperatorType?) {
    POWER(null), TERM(POWER), ARITHMETIC(TERM), INEQUALITY(ARITHMETIC),
    EQUALITY(INEQUALITY), CONJUNCTION(EQUALITY), CLAUSE(CONJUNCTION)
}

fun Operator.getOperatorType(): OperatorType? = when (this) {
    Operator.ADDITION, Operator.SUBTRACTION -> OperatorType.ARITHMETIC
    Operator.MULTIPLICATION, Operator.DIVISION, Operator.MODULO -> OperatorType.TERM
    Operator.EXPONENTIATION -> OperatorType.POWER
    Operator.EQUAL, Operator.NOT_EQUAL -> OperatorType.EQUALITY
    Operator.LESS, Operator.LESS_OR_EQ, Operator.GREATER, Operator.GREATER_OR_EQ -> OperatorType.INEQUALITY
    Operator.OR -> OperatorType.CLAUSE
    Operator.AND -> OperatorType.CONJUNCTION
    else -> null
}
