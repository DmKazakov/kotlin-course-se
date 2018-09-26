package ru.hse.spb.kazakov

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.hse.spb.kazakov.interpreter.Interpreter
import ru.hse.spb.kazakov.lexer.Lexer
import ru.hse.spb.kazakov.parser.Parser

class IntegrationTest {
    private fun getOutput(program: String): String {
        val lexemes = Lexer(program).getLexemes()
        val tree = Parser(lexemes).getTree()
        val output = Interpreter(tree).getOutput()

        return output.joinToString("\n") { list -> list.joinToString(" ") }
    }

    @Test
    fun testIf() {
        val program = """var a = 10
                |var b = 20
                |if (a > b) {
                |    println(1)
                |} else {
                |    println(0)
                |}""".trimMargin()
        assertEquals("0", getOutput(program))
    }

    @Test
    fun testWhile() {
        val program = """var i = 1
                |while (i <= 5) {
                |    println(i)
                |    i = i + 1
                |}""".trimMargin()
        assertEquals("1\n2\n3\n4\n5", getOutput(program))
    }

    @Test
    fun testFunCalls() {
        val program = """fun fib(n) {
                |    if (n <= 1) {
                |        return 1
                |    }
                |    return fib(n - 1) + fib(n - 2)
                |}
                |
                |var i = 1
                |while (i <= 5) {
                |    println(i, fib(i))
                |    i = i + 1
                |}""".trimMargin()
        assertEquals("1 1\n2 2\n3 3\n4 5\n5 8", getOutput(program))
    }

    @Test
    fun testNestedFun() {
        val program = """fun foo(n) {
                |   fun bar(m) {
                |       return m + n
                |   }
                |
                |   return bar(1)
                |}
                |
                |println(foo(41)) // prints 42""".trimMargin()
        assertEquals("42", getOutput(program))
    }

}