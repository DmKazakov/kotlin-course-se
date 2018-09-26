package ru.hse.spb.kazakov.interpreter

import ru.hse.spb.kazakov.InterpretationException
import ru.hse.spb.kazakov.parser.*

class Interpreter(private val root: BlockNode) {
    private var isEvaluated = false
    private val output = mutableListOf<List<Int>>()

    fun getOutput(): List<List<Int>> {
        if (isEvaluated) {
            return output
        }

        val scope = Scope()
        val println = { arguments: List<Int> ->
            output.add(arguments)
            0
        }
        scope.addFunction("println", println)

        evaluateBlock(root, scope)
        isEvaluated = true
        return output
    }

    private fun evaluateBlock(block: BlockNode, scope: Scope): Int? {
        var result: Int? = null
        for ((index, statement) in block.statements.withIndex()) {
            when (statement) {
                is FunDefNode -> evaluateFunDef(statement, scope)
                is VarDefNode -> evaluateVarDef(statement, scope)
                is ExprNode -> evaluateExpr(statement, scope)
                is AssignNode -> evaluateAssign(statement, scope)
                is IfNode -> {
                    val ret = evaluateIf(statement, scope)
                    if (ret != null) {
                        return ret
                    }
                }

                is WhileNode -> {
                    val ret = evaluateWhile(statement, scope)
                    if (ret != null) {
                        return ret
                    }
                }

                is ReturnNode -> {
                    if (!scope.isFunScope) {
                        throw InterpretationException("Error${statement.posInFile}: return isn't allowed in main body.")
                    }
                    if (index != block.statements.lastIndex) {
                        throw InterpretationException("Error${statement.posInFile}: return must be last statement of scope.")
                    }
                    result = evaluateExpr(statement.value, scope)
                }
            }
        }
        return result
    }

    private fun evaluateFunDef(funDef: FunDefNode, scope: Scope) {
        val identifier = funDef.identifier
        if (scope.containsFunction(identifier.value)) {
            throw InterpretationException("Error${identifier.posInFile}: function ${identifier.value} is already defined.")
        }

        val function = { arguments: List<Int> ->
            val paramNum = funDef.parameters.size
            if (arguments.size != paramNum) {
                throw InterpretationException("Error${identifier.posInFile}: function ${identifier.value} has $paramNum parameters.")
            }

            val paramsWithValues = funDef.parameters.zip(arguments)
            val funScope = Scope(scope, true)
            for ((parameter, value) in paramsWithValues) {
                funScope.addVariable(parameter.value, value)
            }
            evaluateBlock(funDef.body, funScope) ?: 0
        }

        scope.addFunction(identifier.value, function)
    }

    private fun evaluateVarDef(varDef: VarDefNode, scope: Scope) {
        assertVarNotInScope(varDef.identifier, scope)
        scope.addVariable(varDef.identifier.value, evaluateExpr(varDef.value, scope))
    }

    private fun assertVarNotInScope(variable: IdentNode, scope: Scope) {
        if (scope.containsVariable(variable.value)) {
            throw InterpretationException(definedVarErrorMessage(variable))
        }
    }

    private fun definedVarErrorMessage(variable: IdentNode): String {
        return "Error${variable.posInFile}: variable ${variable.value} is already defined."
    }

    private fun undefinedVarErrorMessage(variable: IdentNode): String {
        return "Error${variable.posInFile}: variable ${variable.value} is undefined."
    }

    private fun evaluateExpr(expression: ExprNode, scope: Scope): Int = when (expression) {
        is FunCallNode -> {
            val identifier = expression.identifier.value
            val function = scope.getFunction(identifier)
                    ?: throw InterpretationException("Error${expression.posInFile}: function $identifier is undefined.")
            val arguments = mutableListOf<Int>()
            for (argument in expression.args) {
                arguments.add(evaluateExpr(argument, scope))
            }
            function.invoke(arguments)
        }

        is BinaryExprNode -> {
            val firstArgument = evaluateExpr(expression.firstArgument, scope)
            val secondArgument = evaluateExpr(expression.secondArgument, scope)
            expression.operator.evaluate(firstArgument, secondArgument)
        }

        is IdentNode -> scope.getVariable(expression.value)
                ?: throw InterpretationException(undefinedVarErrorMessage(expression))

        is LiteralNode -> expression.value

        else -> throw InterpretationException("Error${expression.posInFile}: undefined expression.")
    }

    private fun evaluateAssign(assignment: AssignNode, scope: Scope) {
        val identifier = assignment.identifier
        if (scope.getVariable(identifier.value) == null) {
            throw InterpretationException(undefinedVarErrorMessage(identifier))
        }
        scope.updateVariable(identifier.value, evaluateExpr(assignment.value, scope))
    }

    private fun evaluateIf(ifNode: IfNode, scope: Scope): Int? {
        val condition = evaluateExpr(ifNode.condition, scope)
        return if (condition == 0) {
            evaluateBlock(ifNode.elseBranch, Scope(scope))
        } else {
            evaluateBlock(ifNode.thenBranch, Scope(scope))
        }
    }

    private fun evaluateWhile(whileNode: WhileNode, scope: Scope): Int? {
        while (evaluateExpr(whileNode.condition, scope) != 0) {
            val ret = evaluateBlock(whileNode.body, Scope(scope))
            if (ret != null) {
                return ret
            }
        }
        return null
    }
}
