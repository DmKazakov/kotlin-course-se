package ru.hse.spb.kazakov.lexer

import org.junit.Assert.*
import org.junit.Test

class LexerUtilsTest {
    @Test
    fun testToLexemeDo() {
        assertNull("do".toLexeme())
    }

    @Test
    fun testToLexemeFalse() {
        assertNull("u".toLexeme())
    }

    @Test
    fun testToLexemePlus() {
        assertEquals(Operator.ADDITION, "+".toLexeme())
    }

    @Test
    fun testToLexemeMinus() {
        assertEquals(Operator.SUBTRACTION, "-".toLexeme())
    }

    @Test
    fun testToLexemeMultiply() {
        assertEquals(Operator.MULTIPLICATION, "*".toLexeme())
    }

    @Test
    fun testToLexemeDivide() {
        assertEquals(Operator.DIVISION, "/".toLexeme())
    }

    @Test
    fun testToLexemeModulo() {
        assertEquals(Operator.MODULO, "%".toLexeme())
    }

    @Test
    fun testToLexemePower() {
        assertEquals(Operator.EXPONENTIATION, "^".toLexeme())
    }

    @Test
    fun testToLexemeAnd() {
        assertEquals(Operator.AND, "&&".toLexeme())
    }

    @Test
    fun testToLexemeOr() {
        assertEquals(Operator.OR, "||".toLexeme())
    }

    @Test
    fun testToLexemeGreater() {
        assertEquals(Operator.GREATER, ">".toLexeme())
    }

    @Test
    fun testToLexemeLess() {
        assertEquals(Operator.LESS, "<".toLexeme())
    }

    @Test
    fun testToLexemeGreaterOrEqual() {
        assertEquals(Operator.GREATER_OR_EQ, ">=".toLexeme())
    }

    @Test
    fun testToLexemeLessOrEqual() {
        assertEquals(Operator.LESS_OR_EQ, "<=".toLexeme())
    }

    @Test
    fun testToLexemeEqual() {
        assertEquals(Operator.EQUAL, "==".toLexeme())
    }

    @Test
    fun testToLexemeNotEqual() {
        assertEquals(Operator.NOT_EQUAL, "!=".toLexeme())
    }

    @Test
    fun testToLexemeIf() {
        assertEquals(Keyword.IF, "if".toLexeme())
    }

    @Test
    fun testToLexemeElse() {
        assertEquals(Keyword.ELSE, "else".toLexeme())
    }

    @Test
    fun testToLexemeWhile() {
        assertEquals(Keyword.WHILE, "while".toLexeme())
    }

    @Test
    fun testToLexemeReturn() {
        assertEquals(Keyword.RETURN, "return".toLexeme())
    }

    @Test
    fun testToLexemeLeftBracket() {
        assertEquals(Separator.LEFT_BRACKET, "(".toLexeme())
    }

    @Test
    fun testToLexemeRightBracket() {
        assertEquals(Separator.RIGHT_BRACKET, ")".toLexeme())
    }

    @Test
    fun testToLexemeComma() {
        assertEquals(Separator.COMMA, ",".toLexeme())
    }

    @Test
    fun testToLexemeFun() {
        assertEquals(Keyword.FUN, "fun".toLexeme())
    }

    @Test
    fun testToLexemeVar() {
        assertEquals(Keyword.VAR, "var".toLexeme())
    }

    @Test
    fun testIsWhitespaceLF() {
        assertFalse('\n'.isSpace())
    }

    @Test
    fun testIsWhitespaceSP() {
        assertTrue(' '.isSpace())
    }

    @Test
    fun testIsWhitespaceHT() {
        assertTrue('\t'.isSpace())
    }

    @Test
    fun testIsWhitespaceFalse() {
        assertFalse('_'.isSpace())
    }

    @Test
    fun testIsIdentifierStartLetter() {
        assertTrue('q'.isIdentifierStart())
    }

    @Test
    fun testIsIdentifierStartUpperCase() {
        assertTrue('P'.isIdentifierStart())
    }

    @Test
    fun testIsIdentifierStartUnderscore() {
        assertTrue('_'.isIdentifierStart())
    }

    @Test
    fun testIsIdentifierStartDigit() {
        assertFalse('1'.isIdentifierStart())
    }

    @Test
    fun testIsIdentifierStartFalse() {
        assertFalse('!'.isIdentifierStart())
    }

    @Test
    fun testIsIdentifierPartLetter() {
        assertTrue('q'.isIdentifierPart())
    }

    @Test
    fun testIsIdentifierPartUpperCase() {
        assertTrue('M'.isIdentifierPart())
    }

    @Test
    fun testIsIdentifierPartUnderscore() {
        assertTrue('_'.isIdentifierPart())
    }

    @Test
    fun testIsIdentifierPartDigit() {
        assertTrue('6'.isIdentifierPart())
    }

    @Test
    fun testIsIdentifierPartFalse() {
        assertFalse('?'.isIdentifierPart())
    }

    @Test
    fun testIsOperatorPartLess() {
        assertTrue('<'.isOperatorStart())
    }

    @Test
    fun testIsOperatorPartGreater() {
        assertTrue('>'.isOperatorStart())
    }

    @Test
    fun testIsOperatorPartEqual() {
        assertTrue('='.isOperatorStart())
    }

    @Test
    fun testIsOperatorPartAnd() {
        assertTrue('&'.isOperatorStart())
    }

    @Test
    fun testIsOperatorPartOr() {
        assertTrue('|'.isOperatorStart())
    }

    @Test
    fun testIsOperatorPartExclamation() {
        assertTrue('!'.isOperatorStart())
    }

    @Test
    fun testIsOperatorPartPlus() {
        assertTrue('+'.isOperatorStart())
    }

    @Test
    fun testIsOperatorPartMinus() {
        assertTrue('-'.isOperatorStart())
    }

    @Test
    fun testIsOperatorPartMultiply() {
        assertTrue('*'.isOperatorStart())
    }

    @Test
    fun testIsOperatorPartDivide() {
        assertTrue('/'.isOperatorStart())
    }

    @Test
    fun testIsOperatorPartModulo() {
        assertTrue('%'.isOperatorStart())
    }

    @Test
    fun testIsOperatorPartPower() {
        assertTrue('^'.isOperatorStart())
    }

    @Test
    fun testIsOperatorPartFalse() {
        assertFalse('?'.isOperatorStart())
    }

    @Test
    fun testIsNumStart() {
        assertTrue('5'.isNumStart())
    }

    @Test
    fun testIsNumStartZero() {
        assertFalse('0'.isNumStart())
    }

    @Test
    fun testIsNumStartFalse() {
        assertFalse('t'.isNumStart())
    }
}
