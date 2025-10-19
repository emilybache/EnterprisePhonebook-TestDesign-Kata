package org.sammancoaching.phonebook


class EnterprisePhonebook(
    val phonebook: Phonebook,
    private val authorizer: Authorizer,
    private val alerter: Alerter,
) {

    fun lookup(name: String): String {
        val authorized: Boolean = authorizer.isAuthorized
        if (!authorized) {
            throw RuntimeException("unauthorized lookup")
        }
        return phonebook.lookup(name)
    }

    fun add(name: String, number: String) {
        val clashes = phonebook.findClashes(number)
        if (clashes.isEmpty()) {
            phonebook.add(name, number)
        } else {
            for (i in 0..<clashes.size) {
                val entry = clashes[i]
                val event = BadPhonebookEntryEvent(
                    phonebook.size(),
                    name,
                    number,
                    entry)
                alerter.sendAlert(event)
            }
        }
    }
}