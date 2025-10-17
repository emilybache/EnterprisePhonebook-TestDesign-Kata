package org.sammancoaching.phonebook

class Phonebook {
    private val storage: MutableMap<String?, String?> = HashMap<String?, String?>()

    fun add(name: String?, number: String?) {
        storage[name] = number
    }

    fun lookup(name: String): String {
        if (!storage.containsKey(name)) {
            throw RuntimeException("unknown name: " + name)
        }
        return storage[name]!!
    }

    fun size(): Int {
        return storage.size
    }

    fun findClashes(numberToCheck: String): MutableList<MutableMap.MutableEntry<String?, String?>> {
        val entries: MutableList<MutableMap.MutableEntry<String?, String?>> = ArrayList()

        for (entry in storage.entries) {
            val number: String = entry.value!!
            if (number.startsWith(numberToCheck) || numberToCheck.startsWith(number)) {
                entries.add(entry)
            }
        }

        return entries
    }
}