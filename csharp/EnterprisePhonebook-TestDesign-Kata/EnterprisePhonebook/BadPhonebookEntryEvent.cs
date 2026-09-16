namespace SammanCoaching.Phonebook;

public sealed record BadPhonebookEntryEvent(
    int PhonebookSize,
    string Name,
    string Number,
    KeyValuePair<string, string> ClashingEntry)
{
    public AlertData ToAlertData() => new(
        PhonebookSize.ToString(),
        $"{Name} cannot be added with number {Number}",
        $"{ClashingEntry.Key} with number {ClashingEntry.Value}");
}
