package org.sammancoaching;

public interface Alerter {
    void sendAlert(BadPhonebookEntryEvent event);
}