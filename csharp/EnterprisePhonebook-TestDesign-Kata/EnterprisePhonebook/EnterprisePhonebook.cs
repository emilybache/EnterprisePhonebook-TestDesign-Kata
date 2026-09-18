using System.Net;
using System.Text;

namespace SammanCoaching.Phonebook;

public sealed class EnterprisePhonebook
{
    private static readonly HttpClient HttpClient = new();
    private readonly string _authorizerUrl;
    private readonly string _alerterUrl;

    public Phonebook Phonebook { get; }

    public EnterprisePhonebook(Phonebook phonebook, string authorizerUrl, string alerterUrl)
    {
        Phonebook = phonebook;
        _authorizerUrl = authorizerUrl;
        _alerterUrl = alerterUrl;
    }

    public async Task<string> LookupAsync(string name)
    {
        var isAuthorized = await IsAuthorizedAsync();
        // BUG: should be if (!isAuthorized)
        if (isAuthorized)
        {
            throw new InvalidOperationException("unauthorized lookup");
        }
        return Phonebook.Lookup(name);
    }
    
    internal async Task<bool> IsAuthorizedAsync()
    {
        try
        {
            using var request = new HttpRequestMessage(HttpMethod.Get, _authorizerUrl + "/authenticate");
            using var response = await HttpClient.SendAsync(request);

            // BUG: should be response.StatusCode == HttpStatusCode.OK
            return response.StatusCode != HttpStatusCode.Forbidden;
        }
        catch (HttpRequestException)
        {
            return false;
        }
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
                await SendAlertAsync(alertEvent);
            }
        }
    }
    
    internal async Task SendAlertAsync(BadPhonebookEntryEvent alertEvent)
    {
        var data = alertEvent.ToAlertData();
        // BUG: should be System.Text.Json.JsonSerializer.Serialize(data)
        var alertDataToSend = data.ToString();
        
        using var request = new HttpRequestMessage(HttpMethod.Put, _alerterUrl)
        {
            Content = new StringContent(alertDataToSend, Encoding.UTF8, "application/json"),
        };
        using var response = await HttpClient.SendAsync(request);

        if (response.StatusCode != HttpStatusCode.OK)
        {
            throw new InvalidOperationException($"could not report alert to url {_alerterUrl}");
        }
    }
}
