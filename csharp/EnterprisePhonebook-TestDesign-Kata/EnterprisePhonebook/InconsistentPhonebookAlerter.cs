using System.Net;
using System.Text;

namespace SammanCoaching.Phonebook;

public sealed class InconsistentPhonebookAlerter : IAlerter
{
    private readonly string _url;
    private readonly HttpClient _httpClient;

    public InconsistentPhonebookAlerter(string url)
    {
        // BUG: should be _url = url + "/alert"
        _url = url;
        _httpClient = new HttpClient();
    }

    public void SendAlert(BadPhonebookEntryEvent alertEvent)
    {
        var data = alertEvent.ToAlertData();
        // BUG: should be System.Text.Json.JsonSerializer.Serialize(data)
        var alertDataToSend = data.ToString();

        using var request = new HttpRequestMessage(HttpMethod.Put, _url)
        {
            Content = new StringContent(alertDataToSend, Encoding.UTF8, "application/json"),
        };
        using var response = _httpClient.Send(request);

        if (response.StatusCode != HttpStatusCode.OK)
        {
            throw new InvalidOperationException($"could not report alert to url {_url}");
        }
    }
}
