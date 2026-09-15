namespace SammanCoaching.Phonebook.Tests;

public class EnterprisePhonebookTest
{
    private sealed class StubAuthorizer : IAuthorizer
    {
        public bool ShouldAuthorize { get; set; } = true;

        public bool IsAuthorized() => ShouldAuthorize;
    }

    private sealed class SpyAlerter : IAlerter
    {
        public List<BadPhonebookEntryEvent> Events { get; } = new();

        public void SendAlert(BadPhonebookEntryEvent alertEvent) => Events.Add(alertEvent);
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
    public void LookupAuthorized()
    {
        _phonebook.Add("Bob", "1234");
        _authorizer.ShouldAuthorize = true;

        Assert.That(_enterprisePhonebook.Lookup("Bob"), Is.EqualTo("1234"));
    }

    [Test]
    public void LookupNotAuthorized()
    {
        _phonebook.Add("Bob", "1234");
        _authorizer.ShouldAuthorize = false;

        Assert.Throws<InvalidOperationException>(() => _enterprisePhonebook.Lookup("Bob"));
    }

    [Test]
    public void AlertInconsistentEntryAttempts()
    {
        _enterprisePhonebook.Add("Bob", "12345");
        _enterprisePhonebook.Add("Sid", "12346");
        _enterprisePhonebook.Add("Ted", "1234");

        var expected1 = new BadPhonebookEntryEvent(2, "Ted", "1234", new("Bob", "12345"));
        var expected2 = new BadPhonebookEntryEvent(2, "Ted", "1234", new("Sid", "12346"));

        Assert.That(_alerter.Events, Is.EqualTo(new[] { expected1, expected2 }));
    }

    [Test]
    public void DoNotAddInconsistentEntry()
    {
        _enterprisePhonebook.Add("Bob", "12345");
        _enterprisePhonebook.Add("Sid", "12346");
        _enterprisePhonebook.Add("Ted", "1234");

        Assert.Throws<KeyNotFoundException>(() => _enterprisePhonebook.Phonebook.Lookup("Ted"));
        Assert.That(_enterprisePhonebook.Phonebook.Count, Is.EqualTo(2));
    }
}
