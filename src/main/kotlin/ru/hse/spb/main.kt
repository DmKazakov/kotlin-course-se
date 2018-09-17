package ru.hse.spb

import java.util.*

private const val NO_SOLUTION_ANSWER = "IMPOSSIBLE"
private const val QUESTION_MARK = '?'

/**
 * Solution for codeforces.com/problemset/problem/59/C?locale=en.
 *
 * @param maxLetterNumber maximum letter number that must be used in the title
 * @param pattern approximate variant of the title
 */
class Problem59C(private val maxLetterNumber: Int, pattern: String) {
    private var isSolved = false
    private var answer: String? = null
    private var unusedLetter = 'a'
    private val pattern: CharArray = pattern.toCharArray()
    private val usedLetters = HashMap<Char, Boolean>()

    /**
     * Returns lexicographically smallest correct title if it's possible, and null otherwise.
     */
    fun solve(): String? {
        if (isSolved) {
            return answer
        }

        if (!completePattern()) {
            return toSolvedState(null)
        }
        markUsedLetters()

        val unusedLettersNumber = maxLetterNumber - usedLetters.size
        val lastIndex = pattern.lastIndex
        val freePosNumber = (0..lastIndex / 2).count { pattern[it] == QUESTION_MARK }
        if (unusedLettersNumber > freePosNumber || unusedLettersNumber < 0) {
            return toSolvedState(null)
        }
        var difference = freePosNumber - unusedLettersNumber
        (0..lastIndex / 2)
                .filter { pattern[it] == '?' }
                .forEach {
                    if (difference > 0) {
                        pattern[it] = 'a'
                        pattern[lastIndex - it] = 'a'
                        difference--
                    } else {
                        updateUnusedLetter()
                        pattern[it] = unusedLetter
                        pattern[lastIndex - it] = unusedLetter
                        usedLetters[unusedLetter] = true
                    }
                }

        return toSolvedState(pattern.joinToString(""))
    }

    /**
     * Change object's state to solved and cashes [answer]
     *
     * @return [answer]
     */
    private fun toSolvedState(answer: String?): String? {
        return answer.also {
            this.answer = answer
            isSolved = true
        }
    }

    /**
     * Replaces question marks with already determined by palindrome condition letters.
     *
     * @return true if the title can be palindrome, and false otherwise
     */
    private fun completePattern(): Boolean {
        val lastIndex = pattern.lastIndex

        for (i in 0..lastIndex / 2) {
            when {
                pattern[i] == QUESTION_MARK -> pattern[i] = pattern[lastIndex - i]
                pattern[lastIndex - i] == QUESTION_MARK -> pattern[lastIndex - i] = pattern[i]
                pattern[i] != pattern[lastIndex - i] -> return false
            }
        }

        return true
    }

    /**
     * Marks used letters in [usedLetters].
     */
    private fun markUsedLetters() {
        val middle = pattern.lastIndex / 2
        (0..middle)
                .filter { pattern[it] != QUESTION_MARK }
                .forEach { usedLetters[pattern[it]] = true }
    }

    /**
     * Updates lexicographically smallest unused letter.
     */
    private fun updateUnusedLetter() {
        while (usedLetters[unusedLetter] == true) {
            unusedLetter = unusedLetter.inc()
        }
    }
}

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val maxLetterNumber = scanner.nextInt()
    val pattern = scanner.next()
    val problem = Problem59C(maxLetterNumber, pattern)
    print(problem.solve() ?: NO_SOLUTION_ANSWER)
}