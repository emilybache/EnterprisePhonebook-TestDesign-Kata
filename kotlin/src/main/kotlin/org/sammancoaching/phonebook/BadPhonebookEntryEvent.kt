package org.sammancoaching.phonebook

@JvmRecord
data class BadPhonebookEntryEvent(
    val phonebookSize: Int, val name: String, val number: String,
    val clashingEntry: Pair<String?, String?>
) {
    fun eventToAlert(): AlertData {
        return AlertData(
            phonebookSize.toString(),
            "$name cannot be added with number $number",
            clashingEntry.first + " with number " + clashingEntry.second
        )
    }
}