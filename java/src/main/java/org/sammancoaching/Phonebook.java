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
            throw new RuntimeException("unknown name: " + name);
        }
        return storage.get(name);
    }

    public int size() {
        return storage.size();
    }


    public List<Map.Entry<String, String>> findClashes(String numberToCheck) {
        List<Map.Entry<String, String>> entries = new ArrayList<>();

        for (Map.Entry<String, String> entry : storage.entrySet()) {
            String number = entry.getValue();
            if (number.startsWith(numberToCheck) || numberToCheck.startsWith(number)) {
                entries.add(entry);
            }
        }

        return entries;
    }
}