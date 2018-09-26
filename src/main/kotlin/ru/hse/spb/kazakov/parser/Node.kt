package ru.hse.spb.kazakov.parser

import ru.hse.spb.kazakov.lexer.Operator
import ru.hse.spb.kazakov.lexer.PosInFile


interface Node

data class BlockNode(val statements: List<StatementNode>) : Node

interface StatementNode : Node {
    val posInFile: PosInFile
}

data class FunDefNode(
        override val posInFile: PosInFile,
        val identifier: IdentNode, val parameters: List<IdentNode>, val body: BlockNode
) : StatementNode

data class VarDefNode(override val posInFile: PosInFile, val identifier: IdentNode, val value: ExprNode) : StatementNode

interface ExprNode : StatementNode

data class WhileNode(override val posInFile: PosInFile, val condition: ExprNode, val body: BlockNode) : StatementNode

data class IfNode(
        override val posInFile: PosInFile,
        val condition: ExprNode, val thenBranch: BlockNode, val elseBranch: BlockNode
) : StatementNode

data class AssignNode(override val posInFile: PosInFile, val identifier: IdentNode, val value: ExprNode) : StatementNode

data class ReturnNode(override val posInFile: PosInFile, val value: ExprNode) : StatementNode

data class FunCallNode(override val posInFile: PosInFile, val identifier: IdentNode, val args: List<ExprNode>) : ExprNode

data class BinaryExprNode(
        override val posInFile: PosInFile,
        val firstArgument: ExprNode, val secondArgument: ExprNode, val operator: Operator
) : ExprNode

data class IdentNode(override val posInFile: PosInFile, val value: String) : ExprNode

data class LiteralNode(override val posInFile: PosInFile, val value: Int) : ExprNode