namespace SammanCoaching.Phonebook;

public sealed class EnterprisePhonebook
{
    private readonly IAuthorizer _authorizer;
    private readonly IAlerter _alerter;

    public Phonebook Phonebook { get; }

    public EnterprisePhonebook(Phonebook phonebook, IAuthorizer authorizer, IAlerter alerter)
    {
        Phonebook = phonebook;
        _authorizer = authorizer;
        _alerter = alerter;
    }

    public async Task<string> LookupAsync(string name)
    {
        if (!await _authorizer.IsAuthorizedAsync())
        {
            throw new InvalidOperationException("unauthorized lookup");
        }
        return Phonebook.Lookup(name);
    }

    public async Task AddAsync(string name, string number)
    {
        var clashes = Phonebook.FindClashes(number);
        if (clashes.Count == 0)
        {
            Phonebook.Add(name, number);
        }
        else
        {
            foreach (var entry in clashes)
            {
                var alertEvent = new BadPhonebookEntryEvent(Phonebook.Count, name, number, entry);
                await _alerter.SendAlertAsync(alertEvent);
            }
        }
    }
}
