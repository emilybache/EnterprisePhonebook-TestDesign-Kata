namespace SammanCoaching.Phonebook;

public sealed class Phonebook
{
    private readonly Dictionary<string, string> _storage = new();

    public void Add(string name, string number) => _storage[name] = number;

    public string Lookup(string name) => _storage[name];

    public int Count => _storage.Count;

    public IReadOnlyList<KeyValuePair<string, string>> FindClashes(string numberToCheck) =>
        _storage
            .Where(entry => entry.Value.StartsWith(numberToCheck) || numberToCheck.StartsWith(entry.Value))
            .ToList();
}
