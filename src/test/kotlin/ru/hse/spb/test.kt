package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test

class TestSource {
    @Test
    fun testFirstExample() {
        val problem = Problem59C(3, "a?c")
        assertEquals("IMPOSSIBLE", problem.solve())
    }

    @Test
    fun testSecondExample() {
        val problem = Problem59C(2, "a??a")
        assertEquals("abba", problem.solve())
    }

    @Test
    fun testThirdExample() {
        val problem = Problem59C(2, "?b?a")
        assertEquals("abba", problem.solve())
    }
}