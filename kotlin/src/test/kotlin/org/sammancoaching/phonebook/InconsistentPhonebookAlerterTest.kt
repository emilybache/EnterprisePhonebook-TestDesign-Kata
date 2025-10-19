package org.sammancoaching.phonebook

import org.junit.jupiter.api.extension.ExtendWith
import org.sammancoaching.testing.HttpServerLifecycle
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(HttpServerLifecycle::class)
class InconsistentPhonebookAlerterTest {
    @Test
    fun triggerAlertOnPotentialClash() {
        val server = HttpServerLifecycle.httpServer!!
        val alerter = InconsistentPhonebookAlerter(server.url)

        val event = BadPhonebookEntryEvent(
            2,
            "Bob",
            "1234",
            Pair("Ted", "123")
        )
        alerter.sendAlert(event)

        assertEquals("""{"size":"2","new_entry":"Bob cannot be added with number 1234","clash":"Ted with number 123"}""",
            server.getHandler().getLatestRequestBody())
    }
}