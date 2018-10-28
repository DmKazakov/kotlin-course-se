package ru.hse.spb.kazakov.interpreter

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import ru.hse.spb.kazakov.InterpretationException
import ru.hse.spb.kazakov.lexer.Operator
import ru.hse.spb.kazakov.lexer.PosInFile
import ru.hse.spb.kazakov.parser.*

class InterpreterTest {
    private val mockPos = PosInFile(0, 0)
    @get:Rule
    val exception = ExpectedException.none()!!

    @Test
    fun testConstructor() {
        Interpreter(BlockNode(emptyList()))
    }

    @Test
    fun testGetOutputEmptyBlock() {
        assertEquals(emptyList<Int>(), Interpreter(BlockNode(emptyList())).getOutput())
    }

    private fun getNum(value: Int): LiteralNode {
        return LiteralNode(mockPos, value)
    }

    private fun StatementNode.toBlock(): BlockNode {
        return BlockNode(listOf(this))
    }

    @Test
    fun testGetOutputNum() {
        assertEquals(emptyList<Int>(), Interpreter(BlockNode(emptyList())).getOutput())
        val literal = getNum(4321)
        assertEquals(emptyList<Int>(), Interpreter(literal.toBlock()).getOutput())
    }

    private fun getIdentifier(name: String, line: Int = 0, pos: Int = 0): IdentNode {
        return IdentNode(PosInFile(line, pos), name)
    }

    private fun getPrintlnVarFunCall(variables: List<String>, line: Int = 0, position: Int = 0): FunCallNode {
        val arguments = mutableListOf<IdentNode>()
        for (variable in variables) {
            arguments.add(getIdentifier(variable))
        }
        val identifier = getIdentifier("println")
        return FunCallNode(PosInFile(line, position), identifier, arguments)
    }

    private fun getPrintlnNumFunCall(values: List<Int>): FunCallNode {
        val arguments = mutableListOf<LiteralNode>()
        for (value in values) {
            arguments.add(getNum(value))
        }
        val identifier = getIdentifier("println")
        return FunCallNode(mockPos, identifier, arguments)
    }

    private fun getVar(name: String, value: Int, line: Int = 0, pos: Int = 0): VarDefNode {
        val identifier = getIdentifier(name)
        val literal = LiteralNode(mockPos, value)
        return VarDefNode(PosInFile(line, pos), identifier, literal)
    }

    @Test
    fun testGetOutputPrintNum() {
        val print321 = getPrintlnNumFunCall(listOf(321))
        assertEquals(listOf(listOf(321)), Interpreter(print321.toBlock()).getOutput())
    }

    @Test
    fun testGetOutputSecondCall() {
        val print321 = getPrintlnNumFunCall(listOf(321))
        val interpreter = Interpreter(print321.toBlock())
        interpreter.getOutput()
        assertEquals(listOf(listOf(321)), interpreter.getOutput())
    }

    @Test
    fun testGetOutputPrintVar() {
        val varNode = getVar("p5", 8764)
        val funCall = getPrintlnVarFunCall(listOf("p5"))
        val block = BlockNode(listOf(varNode, funCall))
        assertEquals(listOf(listOf(8764)), Interpreter(block).getOutput())
    }

    @Test
    fun testGetOutputPrintParentScopeVar() {
        val funCall = getPrintlnVarFunCall(listOf("p5"))
        val varNode = getVar("p5", 8764)
        val ifNode = IfNode(mockPos, getNum(1), funCall.toBlock(), funCall.toBlock())
        val block = BlockNode(listOf(varNode, ifNode))
        assertEquals(listOf(listOf(8764)), Interpreter(block).getOutput())
    }

    @Test
    fun testGetOutputAssign() {
        val varNode = getVar("p5", 8764)
        val assign = AssignNode(mockPos, getIdentifier("p5"), getNum(1))
        val funCall = getPrintlnVarFunCall(listOf("p5"))
        val block = BlockNode(listOf(varNode, assign, funCall))
        assertEquals(listOf(listOf(1)), Interpreter(block).getOutput())
    }

    @Test
    fun testGetOutputAssignInsideInnerScope() {
        val parentVar = getVar("p5", 8764)
        val innerVar = getVar("p5", 54)
        val funCall = getPrintlnVarFunCall(listOf("p5"))
        val ifBlock = BlockNode(listOf(innerVar, funCall))
        val ifNode = IfNode(mockPos, getNum(1), ifBlock, ifBlock)
        val block = BlockNode(listOf(parentVar, ifNode))
        assertEquals(listOf(listOf(54)), Interpreter(block).getOutput())
    }

    @Test
    fun testGetOutputOverwriteParentScopeVariable() {
        val varNode = getVar("p5", 8764)
        val assign = AssignNode(mockPos, getIdentifier("p5"), getNum(1))
        val ifNode = IfNode(mockPos, getNum(1), assign.toBlock(), assign.toBlock())
        val funCall = getPrintlnVarFunCall(listOf("p5"))
        val block = BlockNode(listOf(varNode, ifNode, funCall))
        assertEquals(listOf(listOf(1)), Interpreter(block).getOutput())
    }

    @Test
    fun testGetOutputIfThenBranch() {
        val println7 = getPrintlnNumFunCall(listOf(7))
        val println8 = getPrintlnNumFunCall(listOf(8))
        val ifNode = IfNode(mockPos, getNum(1), println7.toBlock(), println8.toBlock())
        assertEquals(listOf(listOf(7)), Interpreter(ifNode.toBlock()).getOutput())
    }

    @Test
    fun testGetOutputIfElseBranch() {
        val println7 = getPrintlnNumFunCall(listOf(7))
        val println8 = getPrintlnNumFunCall(listOf(8))
        val ifNode = IfNode(mockPos, getNum(0), println7.toBlock(), println8.toBlock())
        assertEquals(listOf(listOf(8)), Interpreter(ifNode.toBlock()).getOutput())
    }

    @Test
    fun testGetOutputWhile() {
        val varNode = getVar("p5", -3)
        val p5 = getIdentifier("p5")
        val plusOne = BinaryExprNode(mockPos, p5, getNum(1), Operator.ADDITION)
        val increment = AssignNode(mockPos, p5, plusOne)
        val funCall = getPrintlnVarFunCall(listOf("p5"))
        val whileNode = WhileNode(mockPos, p5, BlockNode(listOf(increment, funCall)))
        val block = BlockNode(listOf(varNode, whileNode))
        assertEquals(listOf(listOf(-2), listOf(-1), listOf(0)), Interpreter(block).getOutput())
    }

    @Test
    fun testGetOutputFunCall() {
        val print4 = getPrintlnNumFunCall(listOf(4))
        val funName = getIdentifier("foo")
        val funDef = FunDefNode(mockPos, funName, emptyList(), print4.toBlock())
        val funCall = FunCallNode(mockPos, funName, emptyList())
        val block = BlockNode(listOf(funDef, funCall))
        assertEquals(listOf(listOf(4)), Interpreter(block).getOutput())
    }

    @Test
    fun testGetOutputFunCallWithArguments() {
        val funName = getIdentifier("foo")
        val param1 = getIdentifier("p5")
        val param2 = getIdentifier("p4")
        val println = getPrintlnVarFunCall(listOf("p5", "p4"))
        val funDef = FunDefNode(mockPos, funName, listOf(param1, param2), println.toBlock())
        val funCall = FunCallNode(mockPos, funName, listOf(getNum(3), getNum(5)))
        val block = BlockNode(listOf(funDef, funCall))
        assertEquals(listOf(listOf(3, 5)), Interpreter(block).getOutput())
    }

    @Test
    fun testGetOutputFunParameterOverwriteParentScopeVar() {
        val funName = getIdentifier("foo")
        val p5 = getIdentifier("p5")
        val varP5 = getVar("p5", 2)
        val println = getPrintlnVarFunCall(listOf("p5"))
        val funDef = FunDefNode(mockPos, funName, listOf(p5), println.toBlock())
        val funCall = FunCallNode(mockPos, funName, listOf(getNum(3)))
        val block = BlockNode(listOf(funDef, varP5, funCall))
        assertEquals(listOf(listOf(3)), Interpreter(block).getOutput())
    }

    @Test
    fun testGetOutputFunReturn0() {
        val funName = getIdentifier("foo")
        val funDef = FunDefNode(mockPos, funName, emptyList(), BlockNode(emptyList()))
        val funCall = FunCallNode(mockPos, funName, emptyList())
        val println = getIdentifier("println")
        val printlnCall = FunCallNode(mockPos, println, listOf(funCall))
        val block = BlockNode(listOf(funDef, printlnCall))
        assertEquals(listOf(listOf(0)), Interpreter(block).getOutput())
    }

    @Test
    fun testGetOutputFunReturn() {
        val funName = getIdentifier("foo")
        val returnNode = ReturnNode(mockPos, getNum(4))
        val funDef = FunDefNode(mockPos, funName, emptyList(), returnNode.toBlock())
        val funCall = FunCallNode(mockPos, funName, emptyList())
        val println = getIdentifier("println")
        val printlnCall = FunCallNode(mockPos, println, listOf(funCall))
        val block = BlockNode(listOf(funDef, printlnCall))
        assertEquals(listOf(listOf(4)), Interpreter(block).getOutput())
    }

    @Test
    fun testGetOutputOverwriteParentScopeFun() {
        val funName = getIdentifier("foo")
        val returnNode = ReturnNode(mockPos, getNum(4))
        val parentScopeFun = FunDefNode(mockPos, funName, emptyList(), returnNode.toBlock())
        val innerScopeFun = FunDefNode(mockPos, funName, emptyList(), BlockNode(emptyList()))
        val funCall = FunCallNode(mockPos, funName, emptyList())
        val println = getIdentifier("println")
        val printlnCall = FunCallNode(mockPos, println, listOf(funCall))
        val ifBranch = BlockNode(listOf(innerScopeFun, printlnCall))
        val ifNode = IfNode(mockPos, getNum(0), ifBranch, ifBranch)
        val block = BlockNode(listOf(parentScopeFun, ifNode))
        assertEquals(listOf(listOf(0)), Interpreter(block).getOutput())
    }

    @Test
    fun testGetOutputOverwriteReturnFromInnerScope() {
        val ifBranch = ReturnNode(mockPos, getNum(4)).toBlock()
        val ifNode = IfNode(mockPos, getNum(0), BlockNode(emptyList()), ifBranch)

        val funName = getIdentifier("foo")
        val funDef = FunDefNode(mockPos, funName, emptyList(), ifNode.toBlock())
        val funCall = FunCallNode(mockPos, funName, emptyList())

        val println = getIdentifier("println")
        val printlnCall = FunCallNode(mockPos, println, listOf(funCall))

        val block = BlockNode(listOf(funDef, printlnCall))
        assertEquals(listOf(listOf(4)), Interpreter(block).getOutput())
    }

    @Test
    fun testGetOutputUndefinedVar() {
        val p5 = getIdentifier("p5", 1, 1)
        val println = getIdentifier("println")
        val funCall = FunCallNode(mockPos, println, listOf(p5))
        exception.expect(InterpretationException::class.java)
        exception.expectMessage("Error(1, 1): variable p5 is undefined.")
        Interpreter(funCall.toBlock()).getOutput()
    }

    @Test
    fun testGetOutputUndefinedFun() {
        val foo = getIdentifier("foo")
        val funCall = FunCallNode(PosInFile(1, 1), foo, emptyList())
        exception.expect(InterpretationException::class.java)
        exception.expectMessage("Error(1, 1): function foo is undefined.")
        Interpreter(funCall.toBlock()).getOutput()
    }

    @Test
    fun testGetOutputInnerScopeVarAccess() {
        val p5 = getIdentifier("p5", 2, 3)
        val p5Var = getVar("p5", 3)
        val ifNode = IfNode(mockPos, getNum(0), p5Var.toBlock(), p5Var.toBlock())
        val println = getIdentifier("println")
        val printlnCall = FunCallNode(mockPos, println, listOf(p5))
        val block = BlockNode(listOf(ifNode, printlnCall))
        exception.expect(InterpretationException::class.java)
        exception.expectMessage("Error(2, 3): variable p5 is undefined.")
        Interpreter(block).getOutput()
    }

    @Test
    fun testGetOutputInnerScopeFunAccess() {
        val foo = getIdentifier("foo")
        val fooDef = FunDefNode(mockPos, foo, emptyList(), BlockNode(emptyList()))
        val ifNode = IfNode(mockPos, getNum(0), fooDef.toBlock(), fooDef.toBlock())
        val fooCall = FunCallNode(PosInFile(1, 3), foo, emptyList())
        val block = BlockNode(listOf(ifNode, fooCall))
        exception.expect(InterpretationException::class.java)
        exception.expectMessage("Error(1, 3): function foo is undefined.")
        Interpreter(block).getOutput()
    }

    @Test
    fun testGetOutputFunCallBeforeDeclaration() {
        val foo = getIdentifier("foo")
        val fooDef = FunDefNode(mockPos, foo, emptyList(), BlockNode(emptyList()))
        val fooCall = FunCallNode(PosInFile(4, 3), foo, emptyList())
        val block = BlockNode(listOf(fooCall, fooDef))
        exception.expect(InterpretationException::class.java)
        exception.expectMessage("Error(4, 3): function foo is undefined.")
        Interpreter(block).getOutput()
    }

    @Test
    fun testGetOutputVarOverride() {
        val p5 = getIdentifier("p5", 1, 1)
        val p5Var = VarDefNode(mockPos, p5, getNum(1))
        val block = BlockNode(listOf(p5Var, p5Var))
        exception.expect(InterpretationException::class.java)
        exception.expectMessage("Error(1, 1): variable p5 is already defined.")
        Interpreter(block).getOutput()
    }

    @Test
    fun testGetOutputFunOverride() {
        val foo = getIdentifier("foo", 1, 1)
        val fooDef = FunDefNode(mockPos, foo, emptyList(), BlockNode(emptyList()))
        val block = BlockNode(listOf(fooDef, fooDef))
        exception.expect(InterpretationException::class.java)
        exception.expectMessage("Error(1, 1): function foo is already defined.")
        Interpreter(block).getOutput()
    }

    @Test
    fun testGetOutputReturnInMainBody() {
        val returnNode = ReturnNode(PosInFile(1, 2), getNum(1))
        exception.expect(InterpretationException::class.java)
        exception.expectMessage("Error(1, 2): return isn't allowed in main body.")
        Interpreter(returnNode.toBlock()).getOutput()
    }

    @Test
    fun testGetOutputReturnInMainBodyInsideInnerScope() {
        val returnNode = ReturnNode(PosInFile(1, 2), getNum(1))
        val ifNode = IfNode(mockPos, getNum(0), BlockNode(emptyList()), returnNode.toBlock())
        exception.expect(InterpretationException::class.java)
        exception.expectMessage("Error(1, 2): return isn't allowed in main body.")
        Interpreter(ifNode.toBlock()).getOutput()
    }

    @Test
    fun testGetOutputStatementAfterReturn() {
        val foo = getIdentifier("foo")
        val returnNode = ReturnNode(PosInFile(1, 2), getNum(1))
        val p5 = getVar("p5", 1)
        val fooBody = BlockNode(listOf(returnNode, p5))
        val fooDef = FunDefNode(mockPos, foo, emptyList(), fooBody)
        val fooCall = FunCallNode(mockPos, foo, emptyList())
        val block = BlockNode(listOf(fooDef, fooCall))
        exception.expect(InterpretationException::class.java)
        exception.expectMessage("Error(1, 2): return must be last statement of block.")
        Interpreter(block).getOutput()
    }

    @Test
    fun testGetOutputStatementAfterReturnInsideInnerScope() {
        val foo = getIdentifier("foo")
        val returnNode = ReturnNode(PosInFile(1, 2), getNum(1))
        val p5 = getVar("p5", 1)
        val ifBranch = BlockNode(listOf(returnNode, p5))
        val ifNode = IfNode(mockPos, getNum(0), BlockNode(emptyList()), ifBranch)
        val fooDef = FunDefNode(mockPos, foo, emptyList(), ifNode.toBlock())
        val fooCall = FunCallNode(mockPos, foo, emptyList())
        val block = BlockNode(listOf(fooDef, fooCall))
        exception.expect(InterpretationException::class.java)
        exception.expectMessage("Error(1, 2): return must be last statement of block.")
        Interpreter(block).getOutput()
    }


    @Test
    fun testGetOutputNumberOfArgsAndParamsMismatch() {
        val funName = getIdentifier("foo", 5, 6)
        val param1 = getIdentifier("p5")
        val param2 = getIdentifier("p4")
        val println = getPrintlnVarFunCall(listOf("p5", "p4"))
        val funDef = FunDefNode(mockPos, funName, listOf(param1, param2), println.toBlock())
        val funCall = FunCallNode(mockPos, funName, listOf(getNum(3)))
        val block = BlockNode(listOf(funDef, funCall))
        exception.expect(InterpretationException::class.java)
        exception.expectMessage("Error(5, 6): function foo has 2 parameters.")
        Interpreter(block).getOutput()
    }
}

