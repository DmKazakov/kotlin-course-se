package ru.hse.spb.kazakov

import java.io.OutputStream
import java.lang.StringBuilder

interface Render {
    fun append(sting: String)

    fun newLine() {
        append("\n")
    }
}

class StringRender(private val builder: StringBuilder) : Render {
    override fun append(sting: String) {
        builder.append(sting)
    }
}

class StreamRender(private val stream: OutputStream) : Render {
    override fun append(sting: String) {
        stream.write(sting.toByteArray())
    }
}

@DslMarker
annotation class TexMarker

@TexMarker
interface Element {
    fun render(output: Render, ident: String)
}

class TextElement(private val text: String) : Element {
    override fun render(output: Render, ident: String) {
        output.append("$ident$text\n")
    }
}

abstract class MainElement : Element {
    protected fun Render.appendArgument(argument: String?) {
        if (argument != null) {
            append("{$argument}")
        }
    }

    protected fun Render.appendOptions(options: Array<out String>) {
        if (options.isNotEmpty()) {
            append(options.joinToString(",", "[", "]"))
        }
    }

    companion object {
        fun Array<out Pair<String, String>>.toOptionsArray(): Array<String> {
            return this.map { "${it.first}=${it.second}" }.toTypedArray()
        }
    }
}

abstract class OneLineCommand(
    private val name: String,
    private val argument: String? = null,
    private vararg val options: String
) : MainElement() {

    override fun render(output: Render, ident: String) {
        output.append("$ident\\$name")
        output.appendOptions(options)
        output.appendArgument(argument)
        output.newLine()
    }

}

abstract class ExternalCommand(
    private val name: String?,
    private val argument: String?,
    private vararg val options: String
) : MainElement() {

    private val children = mutableListOf<Element>()

    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }

    protected fun renderChildren(output: Render, ident: String) {
        children.forEach { it.render(output, "$ident    ") }
    }

    override fun render(output: Render, ident: String) {
        output.append("$ident\\begin{$name}")
        output.appendOptions(options)
        output.appendArgument(argument)
        output.newLine()

        renderChildren(output, ident)

        output.append("$ident\\end{$name}")
        output.newLine()
    }

    protected fun <T : Element> initElement(element: T, init: T.() -> Unit): T {
        element.init()
        children.add(element)
        return element
    }

    fun flushLeft(init: LeftAlign.() -> Unit) = initElement(LeftAlign(), init)

    fun center(init: CenterAlign.() -> Unit) = initElement(CenterAlign(), init)

    fun flushRight(init: RightAlign.() -> Unit) = initElement(RightAlign(), init)
}

abstract class BlockCommand(
    name: String,
    argument: String?,
    vararg options: String
) : ExternalCommand(name, argument, *options) {

    fun frame(
        name: String? = null, vararg options: String, init: Frame.() -> Unit
    ) = initElement(Frame(name, *options), init)

    fun frame(
        name: String? = null, option: Pair<String, String>, vararg options: Pair<String, String>, init: Frame.() -> Unit
    ) = initElement(Frame(name, option, *options), init)

    fun enumerate(vararg options: String, init: Enumerate.() -> Unit) = initElement(Enumerate(*options), init)

    fun itemize(vararg options: String, init: Itemize.() -> Unit) = initElement(Itemize(*options), init)

    fun math(init: Math.() -> Unit) = initElement(Math(), init)

    fun customTag(
        name: String, vararg options: String, init: CustomTag.() -> Unit
    ) = initElement(CustomTag(name, *options), init)

    fun customTag(
        name: String, vararg options: Pair<String, String>, init: CustomTag.() -> Unit
    ) = initElement(CustomTag(name, *options.toOptionsArray()), init)
}

class Enumerate(vararg options: String) : ListCommand("enumerate", null, *options)

class Itemize(vararg options: String) : ListCommand("itemize", null, *options)

class Frame(frame: String?, vararg options: String) : BlockCommand("frame", frame, *options) {
    constructor(
        frame: String?, vararg options: Pair<String, String>
    ) : this(frame, *options.toOptionsArray())
}

class CustomTag(name: String, vararg options: String) : BlockCommand(name, null, *options)

abstract class ListCommand(
    name: String, argument: String?, vararg options: String
) : ExternalCommand(name, argument, *options) {

    fun item(init: Item.() -> Unit) = initElement(Item(), init)

}

class Math : ExternalCommand("", null) {
    override fun render(output: Render, ident: String) {
        output.append("$$")
        output.newLine()
        renderChildren(output, "")
        output.append("$$")
        output.newLine()
    }
}

class Item : BlockCommand("", null) {
    override fun render(output: Render, ident: String) {
        output.append("$ident\\item")
        output.newLine()
        renderChildren(output, ident)
    }
}

class LeftAlign : BlockCommand("flushleft", null)

class CenterAlign : BlockCommand("center", null)

class RightAlign : BlockCommand("flushright", null)

class DocumentClass(
    documentClass: String, vararg options: String
) : OneLineCommand("documentClass", documentClass, *options)

class Package(packageName: String, vararg options: String) : OneLineCommand("usepackage", packageName, *options) {
    constructor(
        packageName: String, vararg options: Pair<String, String>
    ) : this(packageName, *options.toOptionsArray())
}

class Document : BlockCommand("document", null) {
    private var documentClass: DocumentClass? = null
    private val packages = mutableListOf<Package>()

    override fun render(output: Render, ident: String) {
        if (documentClass == null) {
            throw RenderException("Document must have a document class.")
        }
        documentClass?.render(output, ident)
        packages.forEach { it.render(output, ident) }
        super.render(output, ident)
    }

    fun documentClass(documentClass: String, vararg options: String) {
        if (this.documentClass != null) {
            throw RenderException("Document must have one document class.")
        }
        this.documentClass = DocumentClass(documentClass, *options)
    }

    fun usepackage(packageName: String, vararg options: String) {
        packages.add(Package(packageName, *options))
    }

    fun usepackage(packageName: String, option: Pair<String, String>, vararg options: Pair<String, String>) {
        packages.add(Package(packageName, option, *options))
    }

    override fun toString(): String {
        val render = StringBuilder()
        render(StringRender(render), "")
        return render.toString()
    }

    fun toOutputStream(stream: OutputStream) {
        render(StreamRender(stream), "")
    }
}

fun document(init: Document.() -> Unit) = Document().apply(init)