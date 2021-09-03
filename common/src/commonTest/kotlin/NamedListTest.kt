import com.rnett.spellbook.NamedListImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NamedListTest {

    @Test
    fun testBasics() {
        val list = NamedListImpl<Int>()
        list["one"] = 1
        list["two"] = 2
        assertEquals(listOf("one" to 1, "two" to 2), list)

        assertEquals(1, list["one"])
        assertEquals(2, list["two"])

        assertEquals(0, list.indexOf("one"))
        assertEquals(1, list.indexOf("two"))

        assertTrue("one" in list)
        assertTrue("two" in list)

        assertEquals("one_1", list.newName("one") { "_$it" })
    }

    @Test
    fun testMutating() {
        val list = NamedListImpl<Int>()
        list["one"] = 1
        list["two"] = 2
        list["three"] = 3

        assertEquals(2, list["two"])

        list["two"] = 4

        assertEquals(
            listOf(
                "one" to 1,
                "two" to 4,
                "three" to 3,
            ),
            list
        )
        assertEquals(1, list.indexOf("two"))
        assertEquals(4, list["two"])

        list.rename("two", "four")

        assertEquals(
            listOf(
                "one" to 1,
                "four" to 4,
                "three" to 3,
            ),
            list
        )
        assertEquals(1, list.indexOf("four"))
        assertEquals(4, list["four"])

        list["two"] = 2
        list.setIndex("two", 1)

        assertEquals(
            listOf(
                "one" to 1,
                "two" to 2,
                "four" to 4,
                "three" to 3,
            ),
            list
        )
        assertEquals(1, list.indexOf("two"))
        assertEquals(2, list.indexOf("four"))
        assertEquals(2, list["two"])
        assertEquals(4, list["four"])

        list.setIndex("four", 3)

        assertEquals(
            listOf(
                "one" to 1,
                "two" to 2,
                "three" to 3,
                "four" to 4,
            ),
            list
        )
        assertEquals(1, list.indexOf("two"))
        assertEquals(3, list.indexOf("four"))
        assertEquals(2, list["two"])
        assertEquals(4, list["four"])

        list.swap("one", "three")

        assertEquals(
            listOf(
                "three" to 3,
                "two" to 2,
                "one" to 1,
                "four" to 4,
            ),
            list
        )
        assertEquals(0, list.indexOf("three"))
        assertEquals(2, list.indexOf("one"))
        assertEquals(1, list["one"])
        assertEquals(3, list["three"])

    }

}