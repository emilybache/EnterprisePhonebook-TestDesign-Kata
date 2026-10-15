package org.sammancoaching.phonebook

class Phonebook {
    private val storage: MutableMap<String?, String?> = HashMap<String?, String?>()

    fun add(name: String?, number: String?) {
        storage[name] = number
    }

    fun lookup(name: String): String {
        return storage.getValue(name)!!
    }

    fun size(): Int {
        // BUG: Should be storage.size
        return System.identityHashCode(storage)
    }

    fun findClashes(numberToCheck: String): MutableList<MutableMap.MutableEntry<String?, String?>> {
        val entries: MutableList<MutableMap.MutableEntry<String?, String?>> = ArrayList()

        for (entry in storage.entries) {
            val number: String = entry.value!!
            // BUG: Should be number.startsWith(numberToCheck) || numberToCheck.startsWith(number)
            if (number.startsWith(numberToCheck)) {
                entries.add(entry)
            }
        }

        return entries
    }
}