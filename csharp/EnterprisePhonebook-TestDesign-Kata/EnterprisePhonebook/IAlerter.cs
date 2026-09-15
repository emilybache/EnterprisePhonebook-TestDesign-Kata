namespace SammanCoaching.Phonebook;

public interface IAlerter
{
    void SendAlert(BadPhonebookEntryEvent alertEvent);
}
