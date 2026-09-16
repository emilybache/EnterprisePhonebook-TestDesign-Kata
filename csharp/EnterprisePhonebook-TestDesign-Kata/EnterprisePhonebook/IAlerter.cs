namespace SammanCoaching.Phonebook;

public interface IAlerter
{
    Task SendAlertAsync(BadPhonebookEntryEvent alertEvent);
}
