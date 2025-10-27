package org.sammancoaching;

import java.util.Map;

public record BadPhonebookEntryEvent(int phonebookSize, String name, String number,
                                     Map.Entry<String, String> clashingEntry) {

    public AlertData eventToAlert() {
        return new AlertData(
                String.valueOf(phonebookSize),
                name + " cannot be added with number " + number,
                clashingEntry.getKey() + " with number " + clashingEntry.getValue()
        );
    }
}