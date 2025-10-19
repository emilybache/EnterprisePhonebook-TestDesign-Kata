package org.sammancoaching.phonebook

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.util.Map

class EnterprisePhonebookTest {
    var phonebook: Phonebook = Phonebook()

    class StubAuthorizor(override var isAuthorized: Boolean) : Authorizer {}
    val stubAuthorizor = StubAuthorizor(true)

    class SpyAlerter(): Alerter {
        val clashes: MutableList<BadPhonebookEntryEvent> = ArrayList()
        override fun sendAlert(event: BadPhonebookEntryEvent) {
            clashes.add(event)
        }
    }
    val spyAlerter = SpyAlerter()

    var enterprisePhonebook = EnterprisePhonebook(phonebook, stubAuthorizor, spyAlerter)

    @Test
    fun testLookupAuthorized() {
        phonebook.add("Bob", "1234")

        val result = enterprisePhonebook.lookup("Bob")

        assertEquals("1234", result)
    }

    @Test
    fun testLookupNotAuthorized() {
        phonebook.add("Bob", "1234")
        stubAuthorizor.isAuthorized = false

        val executable = Executable {enterprisePhonebook.lookup("Bob")}

        assertThrows(RuntimeException::class.java, executable)
    }

    @Test
    fun testAlertInconsistentEntryAttempt() {
        enterprisePhonebook.add("Bob", "12345")
        enterprisePhonebook.add("Sid", "12346")

        enterprisePhonebook.add("Ted", "1234")

        val expectedCall1 = BadPhonebookEntryEvent(2, "Ted", "1234", Map.entry<String?, String?>("Bob", "12345"))
        val expectedCall2 = BadPhonebookEntryEvent(2, "Ted", "1234", Map.entry<String?, String?>("Sid", "12346"))
        assertEquals(listOf(expectedCall1, expectedCall2), spyAlerter.clashes.toList())
        // Verify only Bob and Sid are in the phonebook
        assertEquals(2, enterprisePhonebook.phonebook.size())
    }

}