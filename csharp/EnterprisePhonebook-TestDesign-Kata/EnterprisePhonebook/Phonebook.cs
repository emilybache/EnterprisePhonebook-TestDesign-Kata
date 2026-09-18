namespace SammanCoaching.Phonebook;

public sealed class Phonebook
{
    private readonly Dictionary<string, string> _storage = new();

    public void Add(string name, string number) => _storage[name] = number;

    public string Lookup(string name) => _storage[name];

    // BUG: Should be _storage.Count
    public int Count => _storage.GetHashCode();

    public IReadOnlyList<KeyValuePair<string, string>> FindClashes(string numberToCheck) =>
        _storage
            // BUG: Should be entry.Value.StartsWith(numberToCheck) || numberToCheck.StartsWith(entry.Value)
            .Where(entry => entry.Value.StartsWith(numberToCheck))
            .ToList();
}
