using System.Text.Json.Serialization;

namespace SammanCoaching.Phonebook;

public sealed record AlertData(
    [property: JsonPropertyName("size")] string Size,
    [property: JsonPropertyName("new_entry")] string NewEntry,
    [property: JsonPropertyName("clash")] string Clash);
