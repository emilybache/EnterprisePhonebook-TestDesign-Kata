using System.Text.Json;
using SammanCoaching.Phonebook.Tests.Testing;

namespace SammanCoaching.Phonebook.Tests;

[HttpServerLifecycle]
public class InconsistentPhonebookAlerterTest
{
    [Test]
    public void EventToJsonData()
    {
        var alertEvent = new BadPhonebookEntryEvent(10, "Ted", "1234", new("Bob", "1234"));

        var data = alertEvent.ToAlertData();

        Assert.That(data.Size, Is.EqualTo("10"));
        Assert.That(data.NewEntry, Is.EqualTo("Ted cannot be added with number 1234"));
        Assert.That(data.Clash, Is.EqualTo("Bob with number 1234"));
    }

    [Test]
    public void GenerateEvent()
    {
        var server = HttpServerLifecycle.HttpServer;
        var alerter = new InconsistentPhonebookAlerter(server.Url);
        var alertEvent = new BadPhonebookEntryEvent(10, "Ted", "1234", new("Bob", "1234"));

        alerter.SendAlert(alertEvent);

        Assert.That(server.Handler.LatestRequestType, Is.EqualTo("PUT"));
        var receivedData = JsonSerializer.Deserialize<AlertData>(server.Handler.LatestRequestBody!);
        Assert.That(receivedData, Is.EqualTo(alertEvent.ToAlertData()));
    }
}
