package ru.hse.spb

import java.util.*

/**
 * Solution for codeforces.com/problemset/problem/59/C?locale=en.
 *
 * @param maxLetterNumber maximum letter number that must be used in the title
 * @param pattern approximate variant of the title
 */
class Problem59C(private val maxLetterNumber: Int, pattern: String) {
    private var answer: String? = null
    private var unusedLetter = 'a'
    private val pattern: CharArray = pattern.toCharArray()
    private val usedLetters = HashMap<Char, Boolean>()

    /**
     * Returns lexicographically smallest correct title if it's possible, and [noSolutionAnswer] otherwise.
     */
    fun solve(): String {
        val answer = this.answer
        if (answer != null) {
            return answer
        }

        if (!completePattern()) {
            this.answer = noSolutionAnswer
            return noSolutionAnswer
        }
        markUsedLetters()

        val unusedLettersNumber = maxLetterNumber - usedLetters.size
        val lastIndex = pattern.size - 1
        val freePosNumber = (0..lastIndex / 2)
                .filter { pattern[it] == questionMark }
                .count()
        if (unusedLettersNumber > freePosNumber || unusedLettersNumber < 0) {
            this.answer = noSolutionAnswer
            return noSolutionAnswer
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
        this.answer = pattern.joinToString("")

        return pattern.joinToString("")
    }

    /**
     * Replaces question marks with already determined by palindrome condition letters.
     *
     * @return true if the title can be palindrome, and false otherwise
     */
    private fun completePattern(): Boolean {
        val lastIndex = pattern.size - 1

        for (i in 0..lastIndex / 2) {
            when {
                pattern[i] == questionMark -> pattern[i] = pattern[lastIndex - i]
                pattern[lastIndex - i] == questionMark -> pattern[lastIndex - i] = pattern[i]
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
                .filter { pattern[it] != questionMark }
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

    companion object {
        val noSolutionAnswer = "IMPOSSIBLE"
        val questionMark = '?'
    }

}

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val maxLetterNumber = scanner.nextInt()
    val pattern = scanner.next()
    val problem = Problem59C(maxLetterNumber, pattern)
    print(problem.solve())
}