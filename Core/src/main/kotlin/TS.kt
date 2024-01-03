package ink.zerodegress.java2ts

interface TSType {
    fun generateTS(): String
}

class TSString: TSType {
    override fun generateTS(): String {
        return "string"
    }
}

class TSNumber: TSType {
    override fun generateTS(): String {
        return "number"
    }
}

class TSObject(val members: Array<Pair<String, TSType>> = emptyArray(), private val functionsAsMethods: Boolean = true): TSType {
    override fun generateTS(): String {
        return if(members.isEmpty()) {
            "object"
        } else {
            "{ " + members.joinToString("; ") { if(functionsAsMethods && it.second is TSFunction) {
                (it.second as TSFunction).generateTSAsMethod(it.first)
            } else {it.first + ": " + it.second.generateTS()}
            } + " }"
        }
    }
}

class TSOmit(private val a: TSType, private val b: TSType): TSType {
    override fun generateTS(): String {
        return "Omit<" + a.generateTS() + ", " + b.generateTS() + ">"
    }
}

class TSBoolean: TSType {
    override fun generateTS(): String {
        return "boolean"
    }
}


class TSNull: TSType {
    override fun generateTS(): String {
        return "null"
    }
}


class TSUndefined: TSType {
    override fun generateTS(): String {
        return "undefined"
    }
}


class TSSymbol: TSType {
    override fun generateTS(): String {
        return "symbol"
    }
}

class TSBigint: TSType {
    override fun generateTS(): String {
        return "bigint"
    }
}

class TSNever: TSType {
    override fun generateTS(): String {
        return "never"
    }
}
class TSUnknown: TSType {
    override fun generateTS(): String {
        return "unknown"
    }
}

class TSArray(private val itemType: TSType): TSType {
    override fun generateTS(): String {
        return itemType.generateTS() + "[]"
    }
}

class TSVoid: TSType {
    override fun generateTS(): String {
        return "void"
    }
}

class TSFunction(private val parameterTypes: Array<Pair<String, TSType>>, private val returnType: TSType, private val modifier: String? = null): TSType {
    override fun generateTS(): String {
        return "(" + parameterTypes.joinToString(",") { it.first + ": " + it.second.generateTS() } + ") => " + returnType.generateTS()
    }

    fun generateTSAsMethod(name: String): String {
        return if (modifier != null) {
            "$modifier "
        } else { "" } + name + "(" + parameterTypes.joinToString(",") { it.first + ": " + it.second.generateTS() } + "): " + returnType.generateTS()
    }
}

class TSCrossType(private val left: TSType, private val right: TSType): TSType {
    override fun generateTS(): String {
        return "(" + left.generateTS() + ") & (" + right.generateTS() + ")"
    }
}


class TSUnionType(private val left: TSType, private val right: TSType): TSType {
    override fun generateTS(): String {
        return "(" + left.generateTS() + ") | (" + right.generateTS() + ")"
    }
}

class TSKnownType(private val name: String): TSType {
    override fun generateTS(): String {
        return name
    }
}