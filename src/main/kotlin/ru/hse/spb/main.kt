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
            isSolved = true
            return answer
        }
        markUsedLetters()

        val unusedLettersNumber = maxLetterNumber - usedLetters.size
        val lastIndex = pattern.size - 1
        val freePosNumber = (0..lastIndex / 2)
                .filter { pattern[it] == QUESTION_MARK }
                .count()
        if (unusedLettersNumber > freePosNumber || unusedLettersNumber < 0) {
            isSolved = true
            return answer
        }
        var difference = freePosNumber - unusedLettersNumber
        (0..lastIndex / 2)
                .asSequence()
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
        answer = pattern.joinToString("")
        isSolved = true

        return answer
    }

    /**
     * Replaces question marks with letters determined by palindrome condition .
     *
     * @return true if the title can be palindrome, and false otherwise
     */
    private fun completePattern(): Boolean {
        val lastIndex = pattern.size - 1

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
        val middle = (pattern.size - 1) / 2
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
