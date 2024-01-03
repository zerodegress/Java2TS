import ink.zerodegress.java2ts.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class TestTS {
    @Test
    fun testObject() {
        assertEquals("{ a: number }", TSObject(arrayOf(Pair("a", TSNumber()))).generateTS())
        assertEquals("object", TSObject(arrayOf()).generateTS())
        assertEquals("{ a(): void }", TSObject(arrayOf(Pair("a", TSFunction(arrayOf(), TSVoid())))).generateTS())
    }

    @Test
    fun testFunction() {
        assertEquals("(p: string) => void", TSFunction(arrayOf(Pair("p", TSString())), TSVoid()).generateTS())
    }
}