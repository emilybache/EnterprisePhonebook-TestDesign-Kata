namespace SammanCoaching.Phonebook.Tests;

public class EnterprisePhonebookTest
{
    private sealed class StubAuthorizer : IAuthorizer
    {
        public bool ShouldAuthorize { get; set; } = true;

        public Task<bool> IsAuthorizedAsync() => Task.FromResult(ShouldAuthorize);
    }

    private sealed class SpyAlerter : IAlerter
    {
        public List<BadPhonebookEntryEvent> Events { get; } = new();

        public Task SendAlertAsync(BadPhonebookEntryEvent alertEvent)
        {
            Events.Add(alertEvent);
            return Task.CompletedTask;
        }
    }

    private Phonebook _phonebook = null!;
    private StubAuthorizer _authorizer = null!;
    private SpyAlerter _alerter = null!;
    private EnterprisePhonebook _enterprisePhonebook = null!;

    [SetUp]
    public void SetUp()
    {
        _phonebook = new Phonebook();
        _authorizer = new StubAuthorizer();
        _alerter = new SpyAlerter();
        _enterprisePhonebook = new EnterprisePhonebook(_phonebook, _authorizer, _alerter);
    }

    [Test]
    public async Task LookupAuthorized()
    {
        _phonebook.Add("Bob", "1234");
        _authorizer.ShouldAuthorize = true;

        Assert.That(await _enterprisePhonebook.LookupAsync("Bob"), Is.EqualTo("1234"));
    }

    [Test]
    public void LookupNotAuthorized()
    {
        _phonebook.Add("Bob", "1234");
        _authorizer.ShouldAuthorize = false;

        Assert.ThrowsAsync<InvalidOperationException>(() => _enterprisePhonebook.LookupAsync("Bob"));
    }

    [Test]
    public async Task AlertInconsistentEntryAttempts()
    {
        await _enterprisePhonebook.AddAsync("Bob", "12345");
        await _enterprisePhonebook.AddAsync("Sid", "12346");
        await _enterprisePhonebook.AddAsync("Ted", "1234");

        var expected1 = new BadPhonebookEntryEvent(2, "Ted", "1234", new("Bob", "12345"));
        var expected2 = new BadPhonebookEntryEvent(2, "Ted", "1234", new("Sid", "12346"));

        Assert.That(_alerter.Events, Is.EqualTo(new[] { expected1, expected2 }));
    }

    [Test]
    public async Task DoNotAddInconsistentEntry()
    {
        await _enterprisePhonebook.AddAsync("Bob", "12345");
        await _enterprisePhonebook.AddAsync("Sid", "12346");
        await _enterprisePhonebook.AddAsync("Ted", "1234");

        Assert.Throws<KeyNotFoundException>(() => _enterprisePhonebook.Phonebook.Lookup("Ted"));
        Assert.That(_enterprisePhonebook.Phonebook.Count, Is.EqualTo(2));
    }
}
