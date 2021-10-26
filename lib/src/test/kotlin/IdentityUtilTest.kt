import utils.IdentityUtil
import kotlin.test.Test
import kotlin.test.assertTrue

class IdentityUtilTest {

    @Test
    fun testNextID() {
        val generator = IdentityUtil(1)

        var lastMade = ""

        repeat(10) {
            val nextId = generator.generateId()
            assertTrue(nextId != lastMade, "Found duplicated at: $it")
            println("Made ID: $nextId")
            lastMade = nextId
        }
    }
}