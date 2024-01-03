import ink.zerodegress.java2ts.*
import org.junit.jupiter.api.Test

class TestTransform {
    @Test
    fun testSimple() {
        val transformer = JavaClassToTsTypeTransformer()
        transformer.transformCustomJavaClass(Object::class.java)
        println(transformer.generateTS())
    }
}