package org.sammancoaching;

public class EnterprisePhonebook {
    private final Phonebook phonebook;
    private final Authorizer authorizer;
    private final Alerter alerter;


    public EnterprisePhonebook(Phonebook phonebook, Authorizer authorizer, Alerter alerter) {
        this.phonebook = phonebook;
        this.authorizer = authorizer;
        this.alerter = alerter;
    }

    public String lookup(String name) {
        boolean authorized = authorizer.isAuthorized();
        if (!authorized) {
            throw new RuntimeException("unauthorized lookup");
        }
        return phonebook.lookup(name);
    }



    public void add(String name, String number) {
        var clashes = phonebook.findClashes(number);
        if (clashes.isEmpty()) {
            phonebook.add(name, number);
        } else {
            for (int i = 0; i < clashes.size(); i++) {
                var entry = clashes.get(i);
                var event = new BadPhonebookEntryEvent(phonebook.size(), name, number, entry);
                alerter.sendAlert(event);
            }
        }
    }

    // Getter for testing purposes
    Phonebook getPhonebook() {
        return phonebook;
    }

}
