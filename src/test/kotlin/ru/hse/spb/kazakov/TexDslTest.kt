package ru.hse.spb.kazakov

import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayOutputStream
import org.junit.After
import java.io.PrintStream
import org.junit.Before
import org.junit.Rule
import org.junit.rules.ExpectedException


class TexDslTest {
    @get:Rule
    val exception = ExpectedException.none()!!
    private val outContent = ByteArrayOutputStream()

    @Before
    fun setUpStreams() {
        System.setOut(PrintStream(outContent))
    }

    @After
    fun cleanUpStreams() {
        System.setOut(null)
    }

    @Test
    fun testStreamSimpleDocument() {
        document {
            documentClass("beamer")
            +"text"
        }.toOutputStream(System.out)

        assertEquals("""
                    |\documentClass{beamer}
                    |\begin{document}
                    |    text
                    |\end{document}
                    |""".trimMargin(), outContent.toString())
    }

    @Test
    fun testStringSimpleDocument() {
        val result = document {
            documentClass("beamer")
            +"text"
        }.toString()

        assertEquals("""
                    |\documentClass{beamer}
                    |\begin{document}
                    |    text
                    |\end{document}
                    |""".trimMargin(), result)
    }

    @Test
    fun testStreamUsePackages() {
        document {
            documentClass("article")
            usepackage("cmap")
            usepackage("package", "option" to "value")
            usepackage("babel", "russian", "english")
            usepackage("geometry", "left" to "9mm", "right" to "3mm", "top" to "5mm", "bottom" to "6mm")
        }.toOutputStream(System.out)

        assertEquals("""
            |\documentClass{article}
            |\usepackage{cmap}
            |\usepackage[option=value]{package}
            |\usepackage[russian,english]{babel}
            |\usepackage[left=9mm,right=3mm,top=5mm,bottom=6mm]{geometry}
            |\begin{document}
            |\end{document}
            |""".trimMargin(), outContent.toString())
    }

    @Test
    fun testStingUsePackages() {
        val result = document {
            documentClass("article")
            usepackage("cmap")
            usepackage("package", "option" to "value")
            usepackage("babel", "russian", "english")
            usepackage("geometry", "left" to "9mm", "right" to "3mm", "top" to "5mm", "bottom" to "6mm")
        }.toString()

        assertEquals("""
            |\documentClass{article}
            |\usepackage{cmap}
            |\usepackage[option=value]{package}
            |\usepackage[russian,english]{babel}
            |\usepackage[left=9mm,right=3mm,top=5mm,bottom=6mm]{geometry}
            |\begin{document}
            |\end{document}
            |""".trimMargin(), result)
    }

    @Test
    fun testStringFrame() {
        val result = document {
            documentClass("article")
            frame {
                +"frame text"
            }
            frame("title", "plain", "shrink") {
            }
        }.toString()

        assertEquals("""
            |\documentClass{article}
            |\begin{document}
            |    \begin{frame}
            |        frame text
            |    \end{frame}
            |    \begin{frame}[plain,shrink]{title}
            |    \end{frame}
            |\end{document}
            |""".trimMargin(), result)
    }

    @Test
    fun testStreamFrame() {
        document {
            documentClass("article")
            frame {
                +"frame text"
            }
            frame("title", "plain", "shrink") {
            }
        }.toOutputStream(System.out)

        assertEquals("""
            |\documentClass{article}
            |\begin{document}
            |    \begin{frame}
            |        frame text
            |    \end{frame}
            |    \begin{frame}[plain,shrink]{title}
            |    \end{frame}
            |\end{document}
            |""".trimMargin(), outContent.toString())
    }

    @Test
    fun testStringEnumerate() {
        val result = document {
            documentClass("article")
            enumerate {
                item {
                    +"first"
                    +"item"
                }
                item {
                }
            }
            enumerate("I") {
                item {
                }
                item {
                    +"second"
                    +"item"
                }
            }
        }.toString()

        assertEquals("""
            |\documentClass{article}
            |\begin{document}
            |    \begin{enumerate}
            |        \item
            |            first
            |            item
            |        \item
            |    \end{enumerate}
            |    \begin{enumerate}[I]
            |        \item
            |        \item
            |            second
            |            item
            |    \end{enumerate}
            |\end{document}
            |""".trimMargin(), result)
    }

    @Test
    fun testStreamEnumerate() {
        document {
            documentClass("article")
            enumerate {
                item {
                    +"first"
                    +"item"
                }
                item {
                }
            }
            enumerate("I") {
                item {
                }
                item {
                    +"second"
                    +"item"
                }
            }
        }.toOutputStream(System.out)

        assertEquals("""
            |\documentClass{article}
            |\begin{document}
            |    \begin{enumerate}
            |        \item
            |            first
            |            item
            |        \item
            |    \end{enumerate}
            |    \begin{enumerate}[I]
            |        \item
            |        \item
            |            second
            |            item
            |    \end{enumerate}
            |\end{document}
            |""".trimMargin(), outContent.toString())
    }

    @Test
    fun testStringItemize() {
        val result = document {
            documentClass("article")
            itemize {
                item {
                    +"first"
                    +"item"
                }
                item {
                }
            }
            itemize("option") {
                item {
                }
                item {
                    +"second"
                    +"item"
                }
            }
        }.toString()

        assertEquals("""
            |\documentClass{article}
            |\begin{document}
            |    \begin{itemize}
            |        \item
            |            first
            |            item
            |        \item
            |    \end{itemize}
            |    \begin{itemize}[option]
            |        \item
            |        \item
            |            second
            |            item
            |    \end{itemize}
            |\end{document}
            |""".trimMargin(), result)
    }

    @Test
    fun testStreamItemize() {
        document {
            documentClass("article")
            itemize {
                item {
                    +"first"
                    +"item"
                }
                item {
                }
            }
            itemize("option") {
                item {
                }
                item {
                    +"second"
                    +"item"
                }
            }
        }.toOutputStream(System.out)

        assertEquals("""
            |\documentClass{article}
            |\begin{document}
            |    \begin{itemize}
            |        \item
            |            first
            |            item
            |        \item
            |    \end{itemize}
            |    \begin{itemize}[option]
            |        \item
            |        \item
            |            second
            |            item
            |    \end{itemize}
            |\end{document}
            |""".trimMargin(), outContent.toString())
    }

    @Test
    fun testStringMath() {
        val result = document {
            documentClass("article")
            math {
                +"dp[i] = dp[i / 2] + x_i"
            }
        }.toString()

        assertEquals("""
            |\documentClass{article}
            |\begin{document}
            |$$
            |    dp[i] = dp[i / 2] + x_i
            |$$
            |\end{document}
            |""".trimMargin(), result)
    }

    @Test
    fun testStreamMath() {
        document {
            documentClass("article")
            math {
                +"dp[i] = dp[i / 2] + x_i"
            }
        }.toOutputStream(System.out)

        assertEquals("""
            |\documentClass{article}
            |\begin{document}
            |$$
            |    dp[i] = dp[i / 2] + x_i
            |$$
            |\end{document}
            |""".trimMargin(), outContent.toString())
    }

    @Test
    fun testStringAlignment() {
        val result = document {
            documentClass("article")
            flushLeft {
                +"left"
            }
            flushRight {
                +"right"
            }
            center {
                +"center"
            }
        }.toString()

        assertEquals("""
            |\documentClass{article}
            |\begin{document}
            |    \begin{flushleft}
            |        left
            |    \end{flushleft}
            |    \begin{flushright}
            |        right
            |    \end{flushright}
            |    \begin{center}
            |        center
            |    \end{center}
            |\end{document}
            |""".trimMargin(), result)
    }

    @Test
    fun testStreamAlignment() {
        document {
            documentClass("article")
            flushLeft {
                +"left"
            }
            flushRight {
                +"right"
            }
            center {
                +"center"
            }
        }.toOutputStream(System.out)

        assertEquals("""
            |\documentClass{article}
            |\begin{document}
            |    \begin{flushleft}
            |        left
            |    \end{flushleft}
            |    \begin{flushright}
            |        right
            |    \end{flushright}
            |    \begin{center}
            |        center
            |    \end{center}
            |\end{document}
            |""".trimMargin(), outContent.toString())
    }

    @Test
    fun testStringCustomTag() {
        val result = document {
            documentClass("article")
            customTag("pyglist", "language" to "kotlin") {
                +"val a"
            }
            customTag("tag", "option1", "option2") {
            }
        }.toString()

        assertEquals("""
            |\documentClass{article}
            |\begin{document}
            |    \begin{pyglist}[language=kotlin]
            |        val a
            |    \end{pyglist}
            |    \begin{tag}[option1,option2]
            |    \end{tag}
            |\end{document}
            |""".trimMargin(), result)
    }

    @Test
    fun testStreamCustomTag() {
        document {
            documentClass("article")
            customTag("pyglist", "language" to "kotlin") {
                +"val a"
            }
            customTag("tag", "option1", "option2") {
            }
        }.toOutputStream(System.out)

        assertEquals("""
            |\documentClass{article}
            |\begin{document}
            |    \begin{pyglist}[language=kotlin]
            |        val a
            |    \end{pyglist}
            |    \begin{tag}[option1,option2]
            |    \end{tag}
            |\end{document}
            |""".trimMargin(), outContent.toString())
    }

    @Test
    fun testStringMissingDocumentClass() {
        exception.expect(RenderException::class.java)
        exception.expectMessage("Document must have a document class.")
        document {
            customTag("pyglist", "language" to "kotlin") {
                +"val a"
            }
        }.toOutputStream(System.out)
    }

    @Test
    fun testStreamMissingDocumentClass() {
        exception.expect(RenderException::class.java)
        exception.expectMessage("Document must have a document class.")
        document {
            customTag("pyglist", "language" to "kotlin") {
                +"val a"
            }
        }.toString()
    }

    @Test
    fun testStringExtraDocumentClass() {
        exception.expect(RenderException::class.java)
        exception.expectMessage("Document must have one document class.")
        document {
            documentClass("article")
            documentClass("beamer")
            customTag("pyglist", "language" to "kotlin") {
                +"val a"
            }
        }.toOutputStream(System.out)
    }

    @Test
    fun testStreamExtraDocumentClass() {
        exception.expect(RenderException::class.java)
        exception.expectMessage("Document must have one document class.")
        document {
            documentClass("article")
            documentClass("beamer")
            customTag("pyglist", "language" to "kotlin") {
                +"val a"
            }
        }.toString()
    }
}