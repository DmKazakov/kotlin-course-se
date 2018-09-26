package ru.hse.spb.kazakov.interpreter

class Scope(private val parent: Scope? = null, val isFunScope: Boolean = parent?.isFunScope ?: false) {
    private val variables = HashMap<String, Int>()
    private val functions = HashMap<String, (List<Int>) -> Int>()

    fun addVariable(name: String, value: Int) {
        variables[name] = value
    }

    fun updateVariable(name: String, value: Int) {
        if (containsVariable(name)) {
            variables[name] = value
        } else {
            parent?.updateVariable(name, value)
        }
    }

    fun addFunction(name: String, function: (List<Int>) -> Int) {
        functions[name] = function
    }

    fun getVariable(name: String): Int? {
        return if (!containsVariable(name)) {
            parent?.getVariable(name)
        } else {
            variables[name]
        }
    }

    fun getFunction(name: String): ((List<Int>) -> Int)? {
        return if (!containsFunction(name)) {
            parent?.getFunction(name)
        } else {
            functions[name]
        }
    }

    fun containsVariable(name: String) = variables.containsKey(name)

    fun containsFunction(name: String) = functions.containsKey(name)
}