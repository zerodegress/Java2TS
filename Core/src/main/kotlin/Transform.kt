package ink.zerodegress.java2ts

import java.lang.reflect.Modifier

data class JavaClassTSObjects(
    val staticObject: TSType,
    val instanceObject: TSType
)

class JavaClassToTsTypeTransformer {
    private val allJavaClassTSObjects = mutableMapOf<String, JavaClassTSObjects>()

    fun generateTS(): String {
        var tsTypesStr = ""
        for ((name, objects) in allJavaClassTSObjects) {
            val tsObjects = TSObject(arrayOf(Pair("staticObject", objects.staticObject), Pair("instanceObject", objects.instanceObject)))
            tsTypesStr += "type ${getTSKnownTypeNameOfJavaClassName(name)} = " + tsObjects.generateTS() + System.lineSeparator()
        }
        var tsInterfaceStr = "interface AllJavaClasses { "
        for ((name, ) in allJavaClassTSObjects) {
            tsInterfaceStr += "'$name': " + TSKnownType(getTSKnownTypeNameOfJavaClassName(name)).generateTS() + ";"
        }
        tsInterfaceStr += " }"
        return tsTypesStr + tsInterfaceStr
    }
    fun transformCustomJavaClass(javaClass: Class<*>): TSType {
        val staticObjectMembers = mutableListOf<Pair<String, TSType>>()
        val instanceObjectMembers = mutableListOf<Pair<String, TSType>>(Pair("__javaClass", TSKnownType("'${javaClass.name}'")))
        allJavaClassTSObjects[javaClass.name] = JavaClassTSObjects(
            TSObject(),
            TSObject()
        )
        for(field in javaClass.declaredFields) {
            if(!Modifier.isPublic(field.modifiers)) {
                continue
            }
            if(Modifier.isStatic(field.modifiers)) {
                staticObjectMembers.add(Pair(field.name, transformCommonJavaClass(field.type)))
            } else {
                instanceObjectMembers.add(Pair(field.name, transformCommonJavaClass(field.type)))
            }
        }
        for(constructor in javaClass.declaredConstructors) {
            if(!Modifier.isPublic(constructor.modifiers)) {
                continue
            }
            val returnType = TSKnownType("AllJavaClasses['${javaClass.name}']" + "['instanceObject']")
            val parameters = constructor.parameters.map { Pair(it.name, transformCommonJavaClass(it.type)) }
            staticObjectMembers.add(Pair("new", TSFunction(parameters.toTypedArray(), returnType)))
        }
        for (method in javaClass.declaredMethods) {
            if(!Modifier.isPublic(method.modifiers)) {
                continue
            }
            val returnType = transformCommonJavaClass(method.returnType)
            val parameters = method.parameters.map { Pair(it.name, transformCommonJavaClass(it.type)) }

            if(Modifier.isStatic(method.modifiers)) {
                staticObjectMembers.add(Pair(method.name, TSFunction(parameters.toTypedArray(), returnType)))
            } else {
                instanceObjectMembers.add(Pair(method.name, TSFunction(parameters.toTypedArray(), returnType)))
            }
        }
        var instanceType: TSType = TSObject(instanceObjectMembers.toTypedArray())
        if(javaClass.superclass != null) {
            instanceType = TSCrossType(instanceType, TSOmit(transformCommonJavaClass(javaClass.superclass), TSKnownType("'__javaClass'")))
        }
        for (inter in javaClass.interfaces) {
            instanceType = TSCrossType(instanceType, TSOmit(transformCommonJavaClass(inter), TSKnownType("'__javaClass'")))
        }
        allJavaClassTSObjects[javaClass.name] = JavaClassTSObjects(
            TSObject(staticObjectMembers.toTypedArray()),
            instanceType
        )
        return TSKnownType("AllJavaClasses['${javaClass.name}']" + "['instanceObject']")
    }

    fun transformCommonJavaClass(javaClass: Class<*>): TSType {
        if(allJavaClassTSObjects.contains(javaClass.name)) {
            return TSKnownType("AllJavaClasses['${javaClass.name}']" + "['instanceObject']")
        }
        if(javaClass.isArray) {
            return if(javaClass.componentType != null) {
                TSArray(transformCommonJavaClass(javaClass.componentType))
            } else {
                TSArray(TSUnknown())
            }
        }
        return when (javaClass) {
            Void.TYPE, Void::class.java -> TSVoid()
            java.lang.String::class.java, String::class.java -> TSString()
            Byte::class.java, Int::class.java , Long::class.java , Short::class.java , Float::class.java , Double::class.java -> TSNumber()
            Boolean::class.java -> TSBoolean()
            Array::class.java, java.lang.reflect.Array::class.java -> {
                if(javaClass.componentType != null) {
                    TSArray(transformCommonJavaClass(javaClass.componentType))
                } else {
                    TSArray(TSUnknown())
                }
            }
            else -> {
                when(javaClass.name) {
                    "void" -> TSVoid()
                    else -> transformCustomJavaClass(javaClass)
                }
            }
        }
    }

    private fun getTSKnownTypeNameOfJavaClassName(name: String): String {
        return "AllJavaClass_" + name.replace('.', '_').replace("[", "_LB_").replace(";", "_SM_")
    }
}