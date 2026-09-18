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
        var isAuthorized = await _authorizer.IsAuthorizedAsync();
        // BUG: should be if (!isAuthorized)
        if (isAuthorized)
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
            for (var i = 0; i < clashes.Count; i++)
            {
                // BUG: Should be clashes[i]
                var entry = clashes[0];
                var alertEvent = new BadPhonebookEntryEvent(Phonebook.Count, name, number, entry);
                await _alerter.SendAlertAsync(alertEvent);
            }
        }
    }
}
