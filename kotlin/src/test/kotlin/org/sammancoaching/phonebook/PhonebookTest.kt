package org.sammancoaching.phonebook

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.util.Map

class PhonebookTest {
    private var phonebook: Phonebook = Phonebook()

    @BeforeEach
    fun setUp() {
        phonebook = Phonebook()
    }


    @Test
    fun testLookupMissingName() {
        Assertions.assertThrows<RuntimeException?>(
            RuntimeException::class.java,
            Executable { phonebook.lookup("Bob") })
    }


    @Test
    fun testLookUpByName() {
        phonebook.add("Bob", "1234")
        val result = phonebook.lookup("Bob")
        Assertions.assertEquals("1234", result)
    }

    @Test
    fun testNoClashes() {
        phonebook.add("Bob", "12345")
        phonebook.add("Sid", "6789")
        val clashes = phonebook.findClashes("0987")
        Assertions.assertTrue(clashes.isEmpty())
    }

    @Test
    fun testClashIdentical() {
        phonebook.add("Bob", "12345")
        phonebook.add("Sid", "12346")
        val clashes = phonebook.findClashes("12345")

        val expected = listOf(
            Map.entry<String?, String?>("Bob", "12345")
        )

        Assertions.assertIterableEquals(expected, clashes)
    }

    @Test
    fun testSeveralClashes() {
        phonebook.add("Bob", "12345")
        phonebook.add("Sid", "12346")
        val clashes = phonebook.findClashes("1234")

        val expected = listOf(
            Map.entry<String?, String?>("Bob", "12345"),
            Map.entry<String?, String?>("Sid", "12346")
        )

        Assertions.assertIterableEquals(expected, clashes)
    }
}