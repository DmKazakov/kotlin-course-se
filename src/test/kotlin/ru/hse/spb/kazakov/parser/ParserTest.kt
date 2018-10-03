package ru.hse.spb.kazakov.parser

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import ru.hse.spb.kazakov.InterpretationException
import ru.hse.spb.kazakov.lexer.*

class ParserTest {
    @get:Rule
    val exception = ExpectedException.none()!!

    @Test
    fun testConstructor() {
        val input = listOf(
            LexemePosition(Literal(4321), line = 1, begin = 1, end = 4),
            LexemePosition(Separator.EOF, line = 1, begin = 4, end = 4)
        )
        Parser(input)
    }

    @Test
    fun testGetTreeEmpty() {
        assertEquals(BlockNode(emptyList()), Parser(emptyList()).getTree())
    }

    @Test
    fun testGetTreeNum() {
        val input = listOf(
            LexemePosition(Literal(4321), line = 1, begin = 1, end = 4),
            LexemePosition(Separator.EOF, line = 1, begin = 4, end = 4)
        )
        val literal = LiteralNode(input[0].getPosition(), 4321)
            .toBinExprNode(input[1].getPosition())
        assertEquals(BlockNode(listOf(literal)), Parser(input).getTree())
    }

    @Test
    fun testGetTreeSecondCall() {
        val input = listOf(
            LexemePosition(Literal(4321), line = 1, begin = 1, end = 4),
            LexemePosition(Separator.EOF, line = 1, begin = 4, end = 4)
        )
        val literal = LiteralNode(input[0].getPosition(), 4321)
            .toBinExprNode(input[1].getPosition())
        val parser = Parser(input)
        parser.getTree()
        assertEquals(BlockNode(listOf(literal)), parser.getTree())
    }

    @Test
    fun testGetTreeIdentifier() {
        val input = listOf(
            LexemePosition(Identifier("p5"), line = 1, begin = 1, end = 2),
            LexemePosition(Separator.EOF, line = 1, begin = 2, end = 2)
        )
        val identifier = IdentNode(input[0].getPosition(), "p5")
            .toBinExprNode(input[1].getPosition())
        assertEquals(BlockNode(listOf(identifier)), Parser(input).getTree())
    }

    @Test
    fun testGetTreeVar() {
        val input = listOf(
            LexemePosition(Keyword.VAR, line = 1, begin = 1, end = 3),
            LexemePosition(Identifier("p5"), line = 1, begin = 5, end = 6),
            LexemePosition(Operator.ASSIGN, line = 1, begin = 8, end = 8),
            LexemePosition(Literal(8764), line = 1, begin = 10, end = 14),
            LexemePosition(Separator.EOF, line = 1, begin = 14, end = 14)
        )
        val identifier = IdentNode(input[1].getPosition(), "p5")
        val literal = LiteralNode(input[3].getPosition(), 8764)
            .toBinExprNode(input[4].getPosition())
        val varNode = VarDefNode(input[0].getPosition(), identifier, literal)
        assertEquals(BlockNode(listOf(varNode)), Parser(input).getTree())
    }

    @Test
    fun testGetTreeNoAssignment() {
        val input = listOf(
            LexemePosition(Keyword.VAR, line = 1, begin = 1, end = 3),
            LexemePosition(Identifier("p5"), line = 1, begin = 5, end = 6),
            LexemePosition(Separator.EOF, line = 1, begin = 7, end = 7)
        )
        val identifier = IdentNode(input[1].getPosition(), "p5")
        val literal = LiteralNode(input[2].getPosition(), 0)
        val varNode = VarDefNode(input[0].getPosition(), identifier, literal)
        assertEquals(BlockNode(listOf(varNode)), Parser(input).getTree())
    }

    @Test
    fun testGetTreeAssignment() {
        val input = listOf(
            LexemePosition(Identifier("p5"), line = 1, begin = 1, end = 2),
            LexemePosition(Operator.ASSIGN, line = 1, begin = 4, end = 4),
            LexemePosition(Literal(8764), line = 1, begin = 6, end = 9),
            LexemePosition(Separator.EOF, line = 1, begin = 10, end = 10)
        )
        val identifier = IdentNode(input[0].getPosition(), "p5")
        val literal = LiteralNode(input[2].getPosition(), 8764)
            .toBinExprNode(input[3].getPosition())
        val assign = AssignNode(input[1].getPosition(), identifier, literal)

        assertEquals(BlockNode(listOf(assign)), Parser(input).getTree())
    }

    @Test
    fun testGetTreeReturn() {
        val input = listOf(
            LexemePosition(Keyword.RETURN, line = 1, begin = 1, end = 6),
            LexemePosition(Identifier("p5"), line = 1, begin = 8, end = 9),
            LexemePosition(Separator.EOF, line = 1, begin = 10, end = 10)
        )
        val identifier = IdentNode(input[1].getPosition(), "p5")
            .toBinExprNode(input[2].getPosition())
        val returnNode = ReturnNode(input[0].getPosition(), identifier)
        assertEquals(BlockNode(listOf(returnNode)), Parser(input).getTree())
    }

    @Test
    fun testGetTreeFunCall() {
        val input = listOf(
            LexemePosition(Identifier("foo"), line = 1, begin = 1, end = 3),
            LexemePosition(Separator.LEFT_BRACKET, line = 1, begin = 4, end = 4),
            LexemePosition(Identifier("p5"), line = 1, begin = 5, end = 6),
            LexemePosition(Separator.RIGHT_BRACKET, line = 1, begin = 7, end = 7),
            LexemePosition(Separator.EOF, line = 2, begin = 8, end = 8)
        )
        val arguments = listOf(
            IdentNode(input[2].getPosition(), "p5")
                .toBinExprNode(input[3].getPosition())
        )
        val identifier = IdentNode(input[0].getPosition(), "foo")
        val funCall = FunCallNode(input[0].getPosition(), identifier, arguments)
            .toBinExprNode(input[4].getPosition())
        assertEquals(BlockNode(listOf(funCall)), Parser(input).getTree())
    }

    @Test
    fun testGetTreeFunCallNoArguments() {
        val input = listOf(
            LexemePosition(Identifier("foo"), line = 1, begin = 1, end = 3),
            LexemePosition(Separator.LEFT_BRACKET, line = 1, begin = 4, end = 4),
            LexemePosition(Separator.RIGHT_BRACKET, line = 1, begin = 7, end = 7),
            LexemePosition(Separator.EOF, line = 2, begin = 8, end = 8)
        )
        val arguments = emptyList<ExprNode>()
        val identifier = IdentNode(input[0].getPosition(), "foo")
        val funCall = FunCallNode(input[0].getPosition(), identifier, arguments)
            .toBinExprNode(input[3].getPosition())
        assertEquals(BlockNode(listOf(funCall)), Parser(input).getTree())
    }

    @Test
    fun testGetTreeIf() {
        val input = listOf(
            LexemePosition(Keyword.IF, line = 1, begin = 1, end = 2),
            LexemePosition(Separator.LEFT_BRACKET, line = 1, begin = 4, end = 4),
            LexemePosition(Identifier("p5"), line = 1, begin = 5, end = 6),
            LexemePosition(Operator.EQUAL, line = 1, begin = 8, end = 8),
            LexemePosition(Literal(8764), line = 1, begin = 10, end = 14),
            LexemePosition(Separator.RIGHT_BRACKET, line = 1, begin = 15, end = 15),
            LexemePosition(Separator.LEFT_BRACE, line = 1, begin = 17, end = 17),
            LexemePosition(Separator.LF, line = 1, begin = 19, end = 19),
            LexemePosition(Keyword.VAR, line = 2, begin = 1, end = 3),
            LexemePosition(Identifier("p3"), line = 2, begin = 5, end = 6),
            LexemePosition(Operator.ASSIGN, line = 2, begin = 8, end = 8),
            LexemePosition(Literal(8764), line = 1, begin = 10, end = 14),
            LexemePosition(Separator.LF, line = 2, begin = 15, end = 15),
            LexemePosition(Separator.RIGHT_BRACE, line = 3, begin = 1, end = 1),
            LexemePosition(Separator.EOF, line = 3, begin = 2, end = 2)
        )
        val p5 = IdentNode(input[2].getPosition(), "p5").toBinExprNode(input[3].getPosition())
        val literal = LiteralNode(input[4].getPosition(), 8764)
            .toBinExprNode(input[5].getPosition())
        val condition = BinaryExprNode(input[3].getPosition(), p5, literal, Operator.EQUAL)

        val p3 = IdentNode(input[9].getPosition(), "p3")
        val p3Value = LiteralNode(input[11].getPosition(), 8764)
            .toBinExprNode(input[12].getPosition())
        val varNode = VarDefNode(input[8].getPosition(), p3, p3Value)

        val ifNode = IfNode(input[0].getPosition(), condition, BlockNode(listOf(varNode)), BlockNode(emptyList()))
        assertEquals(BlockNode(listOf(ifNode)), Parser(input).getTree())
    }

    @Test
    fun testGetTreeIfElse() {
        val input = listOf(
            LexemePosition(Keyword.IF, line = 1, begin = 1, end = 2),
            LexemePosition(Separator.LEFT_BRACKET, line = 1, begin = 4, end = 4),
            LexemePosition(Identifier("p5"), line = 1, begin = 5, end = 6),
            LexemePosition(Operator.EQUAL, line = 1, begin = 8, end = 8),
            LexemePosition(Literal(8764), line = 1, begin = 10, end = 14),
            LexemePosition(Separator.RIGHT_BRACKET, line = 1, begin = 15, end = 15),
            LexemePosition(Separator.LEFT_BRACE, line = 1, begin = 17, end = 17),
            LexemePosition(Separator.LF, line = 1, begin = 19, end = 19),
            LexemePosition(Keyword.VAR, line = 2, begin = 1, end = 3),
            LexemePosition(Identifier("p3"), line = 2, begin = 5, end = 6),
            LexemePosition(Operator.ASSIGN, line = 2, begin = 8, end = 8),
            LexemePosition(Literal(8764), line = 1, begin = 10, end = 14),
            LexemePosition(Separator.LF, line = 2, begin = 15, end = 15),
            LexemePosition(Separator.RIGHT_BRACE, line = 3, begin = 1, end = 1),
            LexemePosition(Keyword.ELSE, line = 3, begin = 2, end = 5),
            LexemePosition(Separator.LEFT_BRACE, line = 3, begin = 7, end = 7),
            LexemePosition(Separator.LF, line = 3, begin = 8, end = 8),
            LexemePosition(Keyword.VAR, line = 4, begin = 1, end = 3),
            LexemePosition(Identifier("p4"), line = 4, begin = 5, end = 6),
            LexemePosition(Operator.ASSIGN, line = 4, begin = 8, end = 8),
            LexemePosition(Literal(8764), line = 4, begin = 10, end = 14),
            LexemePosition(Separator.LF, line = 4, begin = 15, end = 15),
            LexemePosition(Separator.RIGHT_BRACE, line = 5, begin = 1, end = 1),
            LexemePosition(Separator.EOF, line = 5, begin = 2, end = 2)
        )
        val p5 = IdentNode(input[2].getPosition(), "p5").toBinExprNode(input[3].getPosition())
        val literal = LiteralNode(input[4].getPosition(), 8764)
            .toBinExprNode(input[5].getPosition())
        val condition = BinaryExprNode(input[3].getPosition(), p5, literal, Operator.EQUAL)

        val p3 = IdentNode(input[9].getPosition(), "p3")
        val p3Value = LiteralNode(input[11].getPosition(), 8764)
            .toBinExprNode(input[12].getPosition())
        val thenBranch = BlockNode(listOf(VarDefNode(input[8].getPosition(), p3, p3Value)))

        val p4 = IdentNode(input[18].getPosition(), "p4")
        val p4Value = LiteralNode(input[20].getPosition(), 8764)
            .toBinExprNode(input[21].getPosition())
        val elseBranch = BlockNode(listOf(VarDefNode(input[17].getPosition(), p4, p4Value)))

        val ifNode = IfNode(input[0].getPosition(), condition, thenBranch, elseBranch)
        assertEquals(BlockNode(listOf(ifNode)), Parser(input).getTree())
    }

    @Test
    fun testGetTreeWhile() {
        val input = listOf(
            LexemePosition(Keyword.WHILE, line = 1, begin = 1, end = 5),
            LexemePosition(Separator.LEFT_BRACKET, line = 1, begin = 7, end = 7),
            LexemePosition(Identifier("p5"), line = 1, begin = 8, end = 9),
            LexemePosition(Operator.NOT_EQUAL, line = 1, begin = 11, end = 12),
            LexemePosition(Literal(8764), line = 1, begin = 14, end = 17),
            LexemePosition(Separator.RIGHT_BRACKET, line = 1, begin = 18, end = 18),
            LexemePosition(Separator.LEFT_BRACE, line = 1, begin = 20, end = 20),
            LexemePosition(Separator.LF, line = 1, begin = 21, end = 21),
            LexemePosition(Keyword.VAR, line = 2, begin = 1, end = 3),
            LexemePosition(Identifier("p3"), line = 2, begin = 5, end = 6),
            LexemePosition(Operator.ASSIGN, line = 2, begin = 8, end = 8),
            LexemePosition(Literal(8764), line = 2, begin = 10, end = 14),
            LexemePosition(Separator.LF, line = 2, begin = 15, end = 15),
            LexemePosition(Separator.RIGHT_BRACE, line = 3, begin = 1, end = 1),
            LexemePosition(Separator.EOF, line = 3, begin = 2, end = 2)
        )
        val p5 = IdentNode(input[2].getPosition(), "p5").toBinExprNode(input[3].getPosition())
        val literal = LiteralNode(input[4].getPosition(), 8764)
            .toBinExprNode(input[5].getPosition())
        val condition = BinaryExprNode(input[3].getPosition(), p5, literal, Operator.NOT_EQUAL)

        val p3 = IdentNode(input[9].getPosition(), "p3")
        val p3Value = LiteralNode(input[11].getPosition(), 8764)
            .toBinExprNode(input[12].getPosition())
        val varNode = VarDefNode(input[8].getPosition(), p3, p3Value)

        val whileNode = WhileNode(input[0].getPosition(), condition, BlockNode(listOf(varNode)))
        assertEquals(BlockNode(listOf(whileNode)), Parser(input).getTree())
    }


    @Test
    fun testGetTreeFunDef() {
        val input = listOf(
            LexemePosition(Keyword.FUN, line = 1, begin = 1, end = 3),
            LexemePosition(Identifier("foo"), line = 1, begin = 5, end = 7),
            LexemePosition(Separator.LEFT_BRACKET, line = 1, begin = 8, end = 8),
            LexemePosition(Identifier("n"), line = 1, begin = 9, end = 9),
            LexemePosition(Separator.RIGHT_BRACKET, line = 1, begin = 10, end = 10),
            LexemePosition(Separator.LEFT_BRACE, line = 1, begin = 12, end = 12),
            LexemePosition(Separator.LF, line = 1, begin = 13, end = 13),
            LexemePosition(Keyword.VAR, line = 2, begin = 1, end = 3),
            LexemePosition(Identifier("p5"), line = 2, begin = 5, end = 6),
            LexemePosition(Operator.ASSIGN, line = 2, begin = 8, end = 8),
            LexemePosition(Literal(8764), line = 2, begin = 10, end = 14),
            LexemePosition(Separator.LF, line = 2, begin = 15, end = 15),
            LexemePosition(Separator.RIGHT_BRACE, line = 3, begin = 1, end = 1),
            LexemePosition(Separator.EOF, line = 2, begin = 8, end = 8)
        )
        val funIndent = IdentNode(input[1].getPosition(), "foo")
        val parameter = IdentNode(input[3].getPosition(), "n")

        val p5 = IdentNode(input[8].getPosition(), "p5")
        val p5Value = LiteralNode(input[10].getPosition(), 8764)
            .toBinExprNode(input[11].getPosition())
        val varNode = VarDefNode(input[7].getPosition(), p5, p5Value)

        val funNode = FunDefNode(input[0].getPosition(), funIndent, listOf(parameter), BlockNode(listOf(varNode)))
        assertEquals(BlockNode(listOf(funNode)), Parser(input).getTree())
    }

    /**
     * Test for "3 + (4 - 5) * 6 ^ 3 != p5 || p3 && p4"
     */
    @Test
    fun testGetTreeArithmetic() {
        val input = listOf(
            LexemePosition(Literal(3), line = 1, begin = 1, end = 1),
            LexemePosition(Operator.ADDITION, line = 1, begin = 4, end = 4),
            LexemePosition(Separator.LEFT_BRACKET, line = 1, begin = 6, end = 6),
            LexemePosition(Literal(4), line = 1, begin = 7, end = 7),
            LexemePosition(Operator.SUBTRACTION, line = 1, begin = 9, end = 9),
            LexemePosition(Literal(5), line = 1, begin = 11, end = 11),
            LexemePosition(Separator.RIGHT_BRACKET, line = 1, begin = 12, end = 12),
            LexemePosition(Operator.MULTIPLICATION, line = 1, begin = 14, end = 14),
            LexemePosition(Literal(6), line = 1, begin = 16, end = 16),
            LexemePosition(Operator.EXPONENTIATION, line = 1, begin = 18, end = 18),
            LexemePosition(Literal(3), line = 1, begin = 20, end = 20),
            LexemePosition(Operator.NOT_EQUAL, line = 1, begin = 22, end = 23),
            LexemePosition(Identifier("p5"), line = 1, begin = 25, end = 26),
            LexemePosition(Operator.OR, line = 1, begin = 28, end = 29),
            LexemePosition(Identifier("p3"), line = 1, begin = 31, end = 32),
            LexemePosition(Operator.AND, line = 1, begin = 34, end = 35),
            LexemePosition(Identifier("p4"), line = 1, begin = 37, end = 38),
            LexemePosition(Separator.EOF, line = 1, begin = 10, end = 10)
        )
        val minuend = LiteralNode(input[3].getPosition(), 4).toBinExprNode(input[4].getPosition())
        val subtrahend = LiteralNode(input[5].getPosition(), 5)
            .toBinExprNode(input[6].getPosition())
        val subtraction = BinaryExprNode(input[4].getPosition(), minuend, subtrahend, Operator.SUBTRACTION)
            .toBinExprNode(input[7].getPosition())

        val base = LiteralNode(input[8].getPosition(), 6)
        val exponent = LiteralNode(input[10].getPosition(), 3)
            .toBinExprNode(input[11].getPosition())
        val power = BinaryExprNode(input[9].getPosition(), base, exponent, Operator.EXPONENTIATION)

        val product = BinaryExprNode(input[7].getPosition(), subtraction, power, Operator.MULTIPLICATION)

        val augend = LiteralNode(input[0].getPosition(), 3).toBinExprNode(input[1].getPosition())
        val sum = BinaryExprNode(input[1].getPosition(), augend, product, Operator.ADDITION)

        val p3 = IdentNode(input[14].getPosition(), "p3").toBinExprNode(input[15].getPosition())
        val p4 = IdentNode(input[16].getPosition(), "p4").toBinExprNode(input[17].getPosition())
        val and = BinaryExprNode(input[15].getPosition(), p3, p4, Operator.AND)

        val p5 = IdentNode(input[12].getPosition(), "p5").toBinExprNode(input[13].getPosition())
        val notEqual = BinaryExprNode(input[11].getPosition(), sum, p5, Operator.NOT_EQUAL)

        val or = BinaryExprNode(input[13].getPosition(), notEqual, and, Operator.OR)
        assertEquals(BlockNode(listOf(or)), Parser(input).getTree())
    }

    private fun ExprNode.toBinExprNode(position: PosInFile): BinaryExprNode {
        return BinaryExprNode(position, this, LiteralNode(position, 0), Operator.ADDITION)
    }

    @Test
    fun testGetTreeIncompleteFunCall() {
        val input = listOf(
            LexemePosition(Identifier("foo"), line = 1, begin = 1, end = 3),
            LexemePosition(Separator.LEFT_BRACKET, line = 1, begin = 4, end = 4),
            LexemePosition(Separator.LEFT_BRACKET, line = 1, begin = 5, end = 5),
            LexemePosition(Identifier("p5"), line = 1, begin = 6, end = 7),
            LexemePosition(Separator.RIGHT_BRACKET, line = 1, begin = 8, end = 8),
            LexemePosition(Separator.EOF, line = 2, begin = 9, end = 9)
        )
        exception.expect(InterpretationException::class.java)
        exception.expectMessage("Error:(2, 9) COMMA expected.")
        Parser(input).getTree()
    }

    @Test
    fun testGetTreeUnexpectedIdentifier() {
        val input = listOf(
            LexemePosition(Identifier("foo"), line = 1, begin = 1, end = 3),
            LexemePosition(Identifier("bar"), line = 1, begin = 5, end = 7),
            LexemePosition(Separator.LEFT_BRACKET, line = 1, begin = 8, end = 8),
            LexemePosition(Identifier("p5"), line = 1, begin = 9, end = 10),
            LexemePosition(Separator.RIGHT_BRACKET, line = 1, begin = 11, end = 11),
            LexemePosition(Separator.EOF, line = 2, begin = 12, end = 12)
        )
        exception.expect(InterpretationException::class.java)
        exception.expectMessage("Error:(1, 5) LF expected.")
        Parser(input).getTree()
    }
}