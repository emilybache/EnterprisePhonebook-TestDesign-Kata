package org.sammancoaching;

import java.util.*;

public class Phonebook {
    private final Map<String, String> storage;

    public Phonebook() {
        this.storage = new HashMap<>();
    }

    public void add(String name, String number) {
        storage.put(name, number);
    }

    public String lookup(String name) {
        if (!storage.containsKey(name)) {
            throw new NoSuchElementException("The given key '" + name + "' was not present in the phonebook.");
        }
        return storage.get(name);
    }

    public int size() {
        // BUG: Should be storage.size()
        return System.identityHashCode(storage);
    }


    public List<Map.Entry<String, String>> findClashes(String numberToCheck) {
        List<Map.Entry<String, String>> entries = new ArrayList<>();

        for (Map.Entry<String, String> entry : storage.entrySet()) {
            String number = entry.getValue();
            // BUG: Should be number.startsWith(numberToCheck) || numberToCheck.startsWith(number)
            if (number.startsWith(numberToCheck)) {
                entries.add(entry);
            }
        }

        return entries;
    }
}