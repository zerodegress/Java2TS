package ink.zerodegress.java2ts.cli

import ink.zerodegress.java2ts.JavaClassToTsTypeTransformer
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.io.path.Path


fun main(args: Array<String>) {
    val parser = ArgParser("MyCLI")

    val fromJarsStr by parser.option(ArgType.String, shortName = "j", fullName = "jar", description = "From jars").required()
    val classNamesStr by parser.option(ArgType.String, shortName = "c", fullName = "classNames", description = "Class names needed to transform to ts").required()
    val output by parser.option(ArgType.String, shortName = "o", description = "Output file").required()

    parser.parse(args)
    val transformer = JavaClassToTsTypeTransformer()
    val loader = loaderFromJars(fromJarsStr.split(";").toTypedArray())
    for (className in classNamesStr.split(";")) {
        val clazz = Class.forName(className, true, loader)
        transformer.transformCustomJavaClass(clazz)
    }
    Files.write(Path(output), transformer.generateTS().toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
}

fun loaderFromJars(jarPaths: Array<String>): ClassLoader {
    val urls = mutableListOf<URL>()
    for (jarPath in jarPaths) {
        val jarAbsolutePath = Paths.get(System.getProperty("user.dir"), jarPath).toAbsolutePath().toString()
        val url = URL("file:$jarAbsolutePath")
        urls.add(url)
    }
    val loader = URLClassLoader(urls.toTypedArray())
    return loader
}