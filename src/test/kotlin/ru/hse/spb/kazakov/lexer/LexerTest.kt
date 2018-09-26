package ru.hse.spb.kazakov.lexer

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import ru.hse.spb.kazakov.InterpretationException
import org.junit.rules.ExpectedException

class LexerTest {
    @get:Rule
    val exception = ExpectedException.none()!!

    @Test
    fun testConstructor() {
        Lexer("sth")
    }

    @Test
    fun testGetLexemesEmpty() {
        val lexer = Lexer("")
        val expected = listOf(LexemePosition(Separator.EOF, line = 1, begin = 0, end = 0))
        assertEquals(expected, lexer.getLexemes())
    }

    @Test
    fun testGetLexemesNum() {
        val lexer = Lexer("4321")
        val expected = listOf(
                LexemePosition(Literal(4321), line = 1, begin = 1, end = 4),
                LexemePosition(Separator.EOF, line = 1, begin = 4, end = 4)
        )
        assertEquals(expected, lexer.getLexemes())
    }

    @Test
    fun testGetLexemesSecondCall() {
        val lexer = Lexer("4321")
        val expected = listOf(
                LexemePosition(Literal(4321), line = 1, begin = 1, end = 4),
                LexemePosition(Separator.EOF, line = 1, begin = 4, end = 4)
        )
        lexer.getLexemes()
        assertEquals(expected, lexer.getLexemes())
    }

    @Test
    fun testGetLexemesIdentifier() {
        val lexer = Lexer("x")
        val expected = listOf(
                LexemePosition(Identifier("x"), line = 1, begin = 1, end = 1),
                LexemePosition(Separator.EOF, line = 1, begin = 1, end = 1)
        )
        assertEquals(expected, lexer.getLexemes())
    }

    @Test
    fun testGetLexemesIdentifierUpperCase() {
        val lexer = Lexer("P5")
        val expected = listOf(
                LexemePosition(Identifier("P5"), line = 1, begin = 1, end = 2),
                LexemePosition(Separator.EOF, line = 1, begin = 2, end = 2)
        )
        assertEquals(expected, lexer.getLexemes())
    }

    @Test
    fun testGetLexemesLess() {
        val lexer = Lexer("(kotlin<3)")
        val expected = listOf(
                LexemePosition(Separator.LEFT_BRACKET, line = 1, begin = 1, end = 1),
                LexemePosition(Identifier("kotlin"), line = 1, begin = 2, end = 7),
                LexemePosition(Operator.LESS, line = 1, begin = 8, end = 8),
                LexemePosition(Literal(3), line = 1, begin = 9, end = 9),
                LexemePosition(Separator.RIGHT_BRACKET, line = 1, begin = 10, end = 10),
                LexemePosition(Separator.EOF, line = 1, begin = 10, end = 10)
        )
        assertEquals(expected, lexer.getLexemes())
    }

    @Test
    fun testGetLexemesComma() {
        val lexer = Lexer("do not worry, be happy")
        val expected = listOf(
                LexemePosition(Identifier("do"), line = 1, begin = 1, end = 2),
                LexemePosition(Identifier("not"), line = 1, begin = 4, end = 6),
                LexemePosition(Identifier("worry"), line = 1, begin = 8, end = 12),
                LexemePosition(Separator.COMMA, line = 1, begin = 13, end = 13),
                LexemePosition(Identifier("be"), line = 1, begin = 15, end = 16),
                LexemePosition(Identifier("happy"), line = 1, begin = 18, end = 22),
                LexemePosition(Separator.EOF, line = 1, begin = 22, end = 22)
        )
        assertEquals(expected, lexer.getLexemes())
    }

    @Test
    fun testGetLexemesAnd() {
        val lexer = Lexer("summertime &&sadness")
        val expected = listOf(
                LexemePosition(Identifier("summertime"), line = 1, begin = 1, end = 10),
                LexemePosition(Operator.AND, line = 1, begin = 12, end = 13),
                LexemePosition(Identifier("sadness"), line = 1, begin = 14, end = 20),
                LexemePosition(Separator.EOF, line = 1, begin = 20, end = 20)
        )
        assertEquals(expected, lexer.getLexemes())
    }

    @Test
    fun testGetLexemesEquals() {
        val lexer = Lexer("java==false && kotlin == true")
        val expected = listOf(
                LexemePosition(Identifier("java"), line = 1, begin = 1, end = 4),
                LexemePosition(Operator.EQUAL, line = 1, begin = 5, end = 6),
                LexemePosition(Identifier("false"), line = 1, begin = 7, end = 11),
                LexemePosition(Operator.AND, line = 1, begin = 13, end = 14),
                LexemePosition(Identifier("kotlin"), line = 1, begin = 16, end = 21),
                LexemePosition(Operator.EQUAL, line = 1, begin = 23, end = 24),
                LexemePosition(Identifier("true"), line = 1, begin = 26, end = 29),
                LexemePosition(Separator.EOF, line = 1, begin = 29, end = 29)
        )
        assertEquals(expected, lexer.getLexemes())
    }

    @Test
    fun testGetLexemesLF() {
        val lexer = Lexer("first % line\nSecond^liNe")
        val expected = listOf(
                LexemePosition(Identifier("first"), line = 1, begin = 1, end = 5),
                LexemePosition(Operator.MODULO, line = 1, begin = 7, end = 7),
                LexemePosition(Identifier("line"), line = 1, begin = 9, end = 12),
                LexemePosition(Separator.LF, line = 1, begin = 13, end = 13),
                LexemePosition(Identifier("Second"), line = 2, begin = 1, end = 6),
                LexemePosition(Operator.EXPONENTIATION, line = 2, begin = 7, end = 7),
                LexemePosition(Identifier("liNe"), line = 2, begin = 8, end = 11),
                LexemePosition(Separator.EOF, line = 2, begin = 11, end = 11)
        )
        assertEquals(expected, lexer.getLexemes())
    }

    @Test
    fun testGetLexemesMultiLineMedium() {
        val lexer = Lexer("println(p5)\n\nif(y + 1 == x)\n {var x = 3}\n")
        val expected = listOf(
                LexemePosition(Identifier("println"), line = 1, begin = 1, end = 7),
                LexemePosition(Separator.LEFT_BRACKET, line = 1, begin = 8, end = 8),
                LexemePosition(Identifier("p5"), line = 1, begin = 9, end = 10),
                LexemePosition(Separator.RIGHT_BRACKET, line = 1, begin = 11, end = 11),
                LexemePosition(Separator.LF, line = 1, begin = 12, end = 12),
                LexemePosition(Separator.LF, line = 2, begin = 1, end = 1),
                LexemePosition(Keyword.IF, line = 3, begin = 1, end = 2),
                LexemePosition(Separator.LEFT_BRACKET, line = 3, begin = 3, end = 3),
                LexemePosition(Identifier("y"), line = 3, begin = 4, end = 4),
                LexemePosition(Operator.ADDITION, line = 3, begin = 6, end = 6),
                LexemePosition(Literal(1), line = 3, begin = 8, end = 8),
                LexemePosition(Operator.EQUAL, line = 3, begin = 10, end = 11),
                LexemePosition(Identifier("x"), line = 3, begin = 13, end = 13),
                LexemePosition(Separator.RIGHT_BRACKET, line = 3, begin = 14, end = 14),
                LexemePosition(Separator.LF, line = 3, begin = 15, end = 15),
                LexemePosition(Separator.LEFT_BRACE, line = 4, begin = 2, end = 2),
                LexemePosition(Keyword.VAR, line = 4, begin = 3, end = 5),
                LexemePosition(Identifier("x"), line = 4, begin = 7, end = 7),
                LexemePosition(Operator.ASSIGN, line = 4, begin = 9, end = 9),
                LexemePosition(Literal(3), line = 4, begin = 11, end = 11),
                LexemePosition(Separator.RIGHT_BRACE, line = 4, begin = 12, end = 12),
                LexemePosition(Separator.LF, line = 4, begin = 13, end = 13),
                LexemePosition(Separator.EOF, line = 5, begin = 0, end = 0)
        )
        assertEquals(expected, lexer.getLexemes())
    }

    @Test
    fun testGetLexemesComment() {
        val lexer = Lexer("var x //Kimi no Kioku  \n x>=  760//p3")
        val expected = listOf(
                LexemePosition(Keyword.VAR, line = 1, begin = 1, end = 3),
                LexemePosition(Identifier("x"), line = 1, begin = 5, end = 5),
                LexemePosition(Identifier("x"), line = 2, begin = 2, end = 2),
                LexemePosition(Operator.GREATER_OR_EQ, line = 2, begin = 3, end = 4),
                LexemePosition(Literal(760), line = 2, begin = 7, end = 9),
                LexemePosition(Separator.EOF, line = 2, begin = 13, end = 13)
        )
        assertEquals(expected, lexer.getLexemes())
    }

    @Test
    fun testGetLexemesUnexpectedDot() {
        exception.expect(InterpretationException::class.java)
        exception.expectMessage(getExceptionMessage(1, 4))
        val lexer = Lexer("if .abc")
        lexer.getLexemes()
    }

    @Test
    fun testGetLexemesMalformedNum() {
        exception.expect(InterpretationException::class.java)
        exception.expectMessage(getExceptionMessage(1, 13))
        val lexer = Lexer("val p5 = 543?\nfoo(p5)")
        lexer.getLexemes()
    }

    private fun getExceptionMessage(line: Int, position: Int): String = "Error:($line, $position) failed to tokenize."
}