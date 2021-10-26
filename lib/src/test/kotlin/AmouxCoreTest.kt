import org.junit.Test
import kotlin.test.assertTrue

class AmouxCoreTest {

    @Test
    fun testSampleFunction() {
        val core = AmouxCore()
        assertTrue(core.getVersion() == 21L, "Version didn't function")
    }
}