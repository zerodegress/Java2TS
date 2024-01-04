import ink.zerodegress.java2ts.*
import org.junit.jupiter.api.Test

class TestTransform {
    @Test
    fun testSimple() {
        val transformer = JavaClassToTsTypeTransformer()
        transformer.transformCustomJavaClass(Object::class.java)
        println(transformer.generateTS())
    }

    @Test
    fun testVoid() {
        val transformer = JavaClassToTsTypeTransformer()
        transformer.transformCustomJavaClass(TestTransform::class.java)
        println(transformer.generateTS())
    }

    @Test
    fun testArray() {
        val transformer = JavaClassToTsTypeTransformer()
        transformer.transformCommonJavaClass(TestArray::class.java)
        println(transformer.generateTS())
    }
}
class TestArray {
    public val strarr: Array<String> = emptyArray()
}