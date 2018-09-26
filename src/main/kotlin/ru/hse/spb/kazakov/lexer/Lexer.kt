package ru.hse.spb.kazakov.lexer

import ru.hse.spb.kazakov.InterpretationException


class Lexer(sourceCode: String) {
    private val sourceCode: String = "$sourceCode\u0000"
    private var currentPosition = 0
    private var currentLine = 1
    private var currentPosOnLine = 1
    private val lexemes = mutableListOf<LexemePosition>()
    private val currentChar
        get() = sourceCode[currentPosition]
    private val nextChar
        get() = sourceCode[currentPosition + 1]
    private val exceptionMessage
        get() = "Error:($currentLine, $currentPosOnLine) failed to tokenize."

    fun getLexemes(): List<LexemePosition> {
        if (lexemes.isNotEmpty()) {
            return lexemes
        }

        var prevPosition = currentPosition
        skipSpacesAndComments()
        tokenize()
        while (prevPosition != currentPosition) {
            prevPosition = currentPosition
            skipSpacesAndComments()
            tokenize()
        }

        if (currentPosition != sourceCode.lastIndex) {
            throw InterpretationException(exceptionMessage)
        }

        addLexeme(Separator.EOF)
        return lexemes
    }

    private fun skipSpacesAndComments() {
        var hasSkipped = true

        while (hasSkipped) {
            hasSkipped = false

            if (currentChar == '/' && nextChar == '/') {
                moveChar()
                moveChar()
                while (currentChar != '\n' && currentPosition != sourceCode.lastIndex) {
                    moveChar()
                }
                if (currentChar == '\n') {
                    moveChar()
                    toNewLineState()
                }
                hasSkipped = true
            }

            while (currentChar.isSpace()) {
                moveChar()
                hasSkipped = true
            }
        }
    }

    private fun toNewLineState() {
        currentPosOnLine = 1
        currentLine++
    }

    private fun moveChar() {
        currentPosOnLine++
        currentPosition++
    }

    private fun tokenize() {
        val lexeme = currentChar.toLexeme()
        when {
            lexeme is Separator -> {
                moveChar()
                addLexeme(lexeme)
            }
            currentChar.isDigit() -> tokenizeNum()
            currentChar.isIdentifierStart() -> tokenizeAlphabetic()
            currentChar.isOperatorStart() -> tokenizeOperator()
        }
    }

    private fun tokenizeNum() {
        val numBuilder = StringBuilder()

        while (currentChar.isDigit()) {
            numBuilder.append(currentChar)
            moveChar()
        }
        if (numBuilder.length > 1 && numBuilder[0] == '0') {
            throw InterpretationException("$exceptionMessage Leading zeroes aren't allowed.")
        }

        val value = numBuilder.toString().toInt()
        addLexeme(Literal(value))
    }

    private fun tokenizeAlphabetic() {
        val identBuilder = StringBuilder()

        while (currentChar.isIdentifierPart()) {
            identBuilder.append(currentChar)
            moveChar()
        }

        val token = identBuilder.toString()
        val lexeme = token.toLexeme()
        if (lexeme == null) {
            addLexeme(Identifier(token))
        } else {
            addLexeme(lexeme)
        }
    }

    private fun tokenizeOperator() {
        val prevChar = currentChar
        moveChar()
        val lexeme = "$prevChar$currentChar".toLexeme()
        val prevCharLexeme = prevChar.toLexeme()

        when {
            lexeme != null -> {
                moveChar()
                addLexeme(lexeme)
            }
            prevCharLexeme != null -> addLexeme(prevCharLexeme)
            else -> throw InterpretationException(exceptionMessage)
        }
    }

    private fun addLexeme(lexeme: Lexeme) {
        val lexemePosition = LexemePosition(
                lexeme,
                line = currentLine, begin = currentPosOnLine - lexeme.length, end = currentPosOnLine - 1
        )
        lexemes.add(lexemePosition)

        if (lexeme == Separator.LF) {
            toNewLineState()
        }
    }
}