namespace SammanCoaching.Phonebook.Tests;

public class PhonebookTest
{
    private Phonebook _phonebook = null!;

    [SetUp]
    public void SetUp()
    {
        _phonebook = new Phonebook();
    }

    [Test]
    public void Size()
    {
        Assert.That(_phonebook.Count, Is.EqualTo(0));
        _phonebook.Add("Bob", "1234");
        Assert.That(_phonebook.Count, Is.EqualTo(1));
    }

    [Test]
    public void LookupMissingName()
    {
        Assert.Throws<KeyNotFoundException>(() => _phonebook.Lookup("Bob"));
    }

    [Test]
    public void LookUpByName()
    {
        _phonebook.Add("Bob", "1234");
        var result = _phonebook.Lookup("Bob");
        Assert.That(result, Is.EqualTo("1234"));
    }

    [Test]
    public void LookUpByNameLongerNumber()
    {
        _phonebook.Add("Ann", "12345678");
        var result = _phonebook.Lookup("Ann");
        Assert.That(result, Is.EqualTo("12345678"));
    }

    [Test]
    public void NoClashes()
    {
        _phonebook.Add("Bob", "12345");
        _phonebook.Add("Sid", "6789");
        var clashes = _phonebook.FindClashes("0987");
        Assert.That(clashes, Is.Empty);
    }

    [Test]
    public void ClashIdentical()
    {
        _phonebook.Add("Bob", "12345");
        _phonebook.Add("Sid", "12346");
        var clashes = _phonebook.FindClashes("12345");

        var expected = new[] { new KeyValuePair<string, string>("Bob", "12345") };
        Assert.That(clashes, Is.EqualTo(expected));
    }

    [Test]
    public void SeveralClashes()
    {
        _phonebook.Add("Bob", "12345");
        _phonebook.Add("Sid", "12346");
        var clashes = _phonebook.FindClashes("1234");

        var expected = new[]
        {
            new KeyValuePair<string, string>("Bob", "12345"),
            new KeyValuePair<string, string>("Sid", "12346"),
        };
        Assert.That(clashes, Is.EqualTo(expected));
    }

    [Test]
    public void SeveralClashesOtherWayAround()
    {
        _phonebook.Add("Bob", "1234");
        var clashes = _phonebook.FindClashes("123456");

        var expected = new[] { new KeyValuePair<string, string>("Bob", "1234") };
        Assert.That(clashes, Is.EqualTo(expected));
    }
}
