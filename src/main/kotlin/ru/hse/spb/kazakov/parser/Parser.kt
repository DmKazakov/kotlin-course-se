package ru.hse.spb.kazakov.parser

import ru.hse.spb.kazakov.InterpretationException
import ru.hse.spb.kazakov.lexer.*

/**
 * Grammar:
 * S                 -> BLOCK
 * BLOCK             -> (STATEMENT)*
 * BLOCK_WITH_BRACES -> "{" BLOCK "}"
 * STATEMENT         -> FUNCTION | VARIABLE | EXPRESSION | WHILE | IF | ASSIGNMENT | RETURN
 * FUNCTION          -> "fun" IDENTIFIER PARAMETERS BLOCK_WITH_BRACES
 * VARIABLE          -> "var" IDENTIFIER ("=" EXPRESSION)?
 * PARAMETER_NAMES   -> "(" IDENTIFIER{,} ")"
 * WHILE             -> "while" "(" EXPRESSION ")" BLOCK_WITH_BRACES
 * IF                -> "if" "(" EXPRESSION ")" BLOCK_WITH_BRACES ("else" BLOCK_WITH_BRACES)?
 * ASSIGNMENT        -> IDENTIFIER "=" EXPRESSION
 * RETURN            -> "return" EXPRESSION
 * EXPRESSION        -> BINARY_EXPRESSION
 * FUNCTION_CALL     -> IDENTIFIER "(" ARGUMENTS ")"
 * ARGUMENTS         -> EXPRESSION{","}
 * BINARY_EXPRESSION -> CLAUSE
 * CLAUSE            -> CONJUNCTION "||" CLAUSE | CONJUNCTION
 * CONJUNCTION       -> CONJUNCTION "&&" EQUALITY | EQUALITY
 * EQUALITY          -> EQUALITY [==, !=] INEQUALITY | INEQUALITY
 * INEQUALITY        -> INEQUALITY [<, ,<=, >, >= ] ARITHMETIC | ARITHMETIC
 * ARITHMETIC        -> ARITHMETIC [+, -] TERM | TERM
 * TERM              -> TERM [*, /, %] POWER | POWER
 * POWER             -> ATOM "^" POWER | ATOM
 * ATOM              -> LITERAL | IDENTIFIER | FUNCTION_CALL | "(" EXPRESSION ")"
 */
class Parser(private val lexemes: List<LexemePosition>) {
    private var currentPosition = 0
    private var root: BlockNode? = null
    private val currentLexemePos
        get() = lexemes[currentPosition]
    private val currentLexeme
        get() = currentLexemePos.lexeme
    private val posInFile
        get() = currentLexemePos.getPosition()

    fun getTree(): BlockNode {
        if (lexemes.isEmpty()) {
            root = BlockNode(emptyList())
        }
        if (root != null) {
            return root as BlockNode
        }

        val statements = mutableListOf<StatementNode>()
        while (!checkLexeme(Separator.EOF)) {
            statements.add(parseStatement())
        }
        root = BlockNode(statements)

        return root as BlockNode
    }

    private fun checkLexeme(lexeme: Lexeme): Boolean = currentLexeme == lexeme

    private fun isIdentifier(): Boolean = currentLexeme is Identifier

    private fun checkNextLexeme(lexeme: Lexeme): Boolean = lexemes[currentPosition + 1].lexeme == lexeme

    private fun assertLexeme(lexeme: Lexeme): LexemePosition {
        if (!checkLexeme(lexeme)) {
            throw InterpretationException(exceptionMessage(lexeme))
        } else {
            return currentLexemePos.also { currentPosition++ }
        }
    }

    private fun assertIdentifier(): Identifier {
        val lexeme = currentLexeme
        if (lexeme is Identifier) {
            return lexeme.also { currentPosition++ }
        } else {
            throw InterpretationException("Error:${currentLexemePos.getPosition()} identifier expected.")
        }
    }

    private fun exceptionMessage(expected: Lexeme): String {
        return "Error:${currentLexemePos.getPosition()} $expected expected."
    }

    private fun parseStatement(): StatementNode {
        while (checkLexeme(Separator.LF)) {
            currentPosition++
        }

        val result = when {
            checkLexeme(Keyword.IF) -> parseIf()
            checkLexeme(Keyword.WHILE) -> parseWhile()
            checkLexeme(Keyword.FUN) -> parseFunDef()
            checkLexeme(Keyword.VAR) -> parseVar()
            isIdentifier() && checkNextLexeme(Operator.ASSIGN) -> parseAssignment()
            checkLexeme(Keyword.RETURN) -> parseReturn()
            else -> parseExpression()
        }
        if (!checkLexeme(Separator.LF) && !checkLexeme(Separator.EOF)) {
            throw InterpretationException(exceptionMessage(Separator.LF))
        } else if (checkLexeme(Separator.LF)) {
            currentPosition++
        }
        return result
    }

    private fun parseIf(): IfNode {
        val ifLexeme = assertLexeme(Keyword.IF)
        assertLexeme(Separator.LEFT_BRACKET)
        val condition = parseExpression()
        assertLexeme(Separator.RIGHT_BRACKET)
        val thenBranch = parseBlock()
        val elseBranch = if (checkLexeme(Keyword.ELSE)) {
            currentPosition++
            parseBlock()
        } else {
            BlockNode(emptyList())
        }
        return IfNode(ifLexeme.getPosition(), condition, thenBranch, elseBranch)
    }

    private fun parseWhile(): WhileNode {
        val whileLexeme = assertLexeme(Keyword.WHILE)
        assertLexeme(Separator.LEFT_BRACKET)
        val condition = parseExpression()
        assertLexeme(Separator.RIGHT_BRACKET)
        val body = parseBlock()
        return WhileNode(whileLexeme.getPosition(), condition, body)
    }

    private fun parseFunDef(): FunDefNode {
        val funLexeme = assertLexeme(Keyword.FUN)
        val identifier = parseIdentifier()
        val parameters = parseParameters()
        val body = parseBlock()
        return FunDefNode(funLexeme.getPosition(), identifier, parameters, body)
    }

    private fun parseIdentifier(): IdentNode {
        val lexemePos = currentLexemePos
        val identLexeme = assertIdentifier()
        return IdentNode(lexemePos.getPosition(), identLexeme.value)
    }

    private fun parseParameters(): List<IdentNode> {
        val parameters = mutableListOf<IdentNode>()

        assertLexeme(Separator.LEFT_BRACKET)
        if (checkLexeme(Separator.RIGHT_BRACKET)) {
            return parameters.also { currentPosition++ }
        }
        parameters.add(parseIdentifier())
        while (!checkLexeme(Separator.RIGHT_BRACKET)) {
            assertLexeme(Separator.COMMA)
            parameters.add(parseIdentifier())
        }

        return parameters.also { currentPosition++ }
    }

    private fun parseBlock(): BlockNode {
        val statements = mutableListOf<StatementNode>()

        assertLexeme(Separator.LEFT_BRACE)
        if (checkLexeme(Separator.LF)) {
            currentPosition++
        }
        while (!checkLexeme(Separator.RIGHT_BRACE)) {
            if (checkLexeme(Separator.EOF)) {
                throw InterpretationException(exceptionMessage(Separator.RIGHT_BRACE))
            }
            statements.add(parseStatement())
        }

        return BlockNode(statements).also { currentPosition++ }
    }

    private fun parseVar(): VarDefNode {
        val varLexeme = assertLexeme(Keyword.VAR)

        val identifier = parseIdentifier()
        val expression= if (checkLexeme(Operator.ASSIGN)) {
            currentPosition++
            parseExpression()
        } else {
            LiteralNode(currentLexemePos.getPosition(), 0)
        }

        return VarDefNode(varLexeme.getPosition(), identifier, expression)
    }

    private fun parseAssignment(): AssignNode {
        val identifier = parseIdentifier()
        val assignLexeme = assertLexeme(Operator.ASSIGN)
        val expression = parseExpression()
        return AssignNode(assignLexeme.getPosition(), identifier, expression)
    }

    private fun parseReturn(): ReturnNode {
        val result = assertLexeme(Keyword.RETURN)
        return ReturnNode(result.getPosition(), parseExpression())
    }

    private fun parseExpression(): ExprNode  = parseBinaryExpression()

    private fun parseBinaryExpression(): BinaryExprNode {
        return parsePrecedence(OperatorType.CLAUSE)
    }

    private fun parsePrecedence(precedence: OperatorType?): BinaryExprNode {
        val dec = precedence?.dec
        var firstArgument = parseDec(dec)

        var lexeme = currentLexeme
        while (lexeme is Operator && lexeme.getOperatorType() == precedence) {
            val operatorPos = posInFile
            currentPosition++
            val secondArgument = parseDec(dec)
            firstArgument = BinaryExprNode(operatorPos, firstArgument, secondArgument, lexeme)
            lexeme = currentLexeme
        }

        return firstArgument
    }

    private fun parseDec(dec: OperatorType?) = if (dec == OperatorType.POWER) parsePower() else parsePrecedence(dec)

    private fun parsePower(): BinaryExprNode {
        val firstArgument = parseAtom()

        val lexeme = currentLexeme
        if (lexeme is Operator && lexeme.getOperatorType() == OperatorType.POWER) {
            val operatorPos = posInFile
            currentPosition++
            val secondArgument = parsePower()
            return BinaryExprNode(operatorPos, firstArgument, secondArgument, lexeme)
        }

        return mockBinaryExprNode(firstArgument)
    }

    private fun parseAtom(): ExprNode {
        val lexeme = currentLexeme
        return when (lexeme) {
            is Identifier -> if (checkNextLexeme(Separator.LEFT_BRACKET)) parseFunCall() else parseIdentifier()
            is Literal -> LiteralNode(currentLexemePos.getPosition(), lexeme.value).also { currentPosition++ }
            Separator.LEFT_BRACKET -> {
                currentPosition++
                parseExpression().also { assertLexeme(Separator.RIGHT_BRACKET) }
            }

            else -> throw InterpretationException("Error:${currentLexemePos.getPosition()} expression expected.")
        }
    }

    private fun parseFunCall(): FunCallNode {
        val identifier = parseIdentifier()
        val arguments = parseArguments()
        return FunCallNode(identifier.posInFile, identifier, arguments)
    }

    private fun parseArguments(): List<ExprNode> {
        val arguments = mutableListOf<ExprNode>()

        assertLexeme(Separator.LEFT_BRACKET)
        if (checkLexeme(Separator.RIGHT_BRACKET)) {
            return arguments.also { currentPosition++ }
        }

        arguments.add(parseExpression())
        while (!checkLexeme(Separator.RIGHT_BRACKET)) {
            assertLexeme(Separator.COMMA)
            arguments.add(parseExpression())
        }

        return arguments.also { currentPosition++ }
    }

    private fun mockBinaryExprNode(firstArgument: ExprNode): BinaryExprNode {
        return BinaryExprNode(posInFile, firstArgument, LiteralNode(posInFile, 0), Operator.ADDITION)
    }
}