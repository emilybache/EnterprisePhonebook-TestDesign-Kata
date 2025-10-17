package org.sammancoaching.phonebook

interface Alerter {
    fun sendAlert(event: BadPhonebookEntryEvent)
}