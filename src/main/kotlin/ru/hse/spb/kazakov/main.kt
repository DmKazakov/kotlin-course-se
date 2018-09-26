package ru.hse.spb.kazakov

import ru.hse.spb.kazakov.interpreter.Interpreter
import ru.hse.spb.kazakov.lexer.Lexer
import ru.hse.spb.kazakov.parser.Parser
import java.io.File
import java.io.IOException

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("File name isn't specified.")
        return
    }

    val fileContent = try {
        File(args[0]).readText()
    } catch (exception: IOException) {
        println("Unable to read file.")
        return
    }

    val output = try {
        val lexemes = Lexer(fileContent).getLexemes()
        val tree = Parser(lexemes).getTree()
        Interpreter(tree).getOutput()
    } catch (exception: InterpretationException) {
        println(exception.message)
        return
    }

    for (list in output) {
        println(list.joinToString(" "))
    }
}