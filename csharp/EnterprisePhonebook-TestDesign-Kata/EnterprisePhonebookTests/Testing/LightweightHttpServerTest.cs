using System.Text;

namespace SammanCoaching.Phonebook.Tests.Testing;

[HttpServerLifecycle]
public class LightweightHttpServerTest
{
    [Test]
    public void GetRequest()
    {
        var server = HttpServerLifecycle.HttpServer;
        using var client = new HttpClient();
        using var request = new HttpRequestMessage(HttpMethod.Get, server.Url + "/test");

        using var response = client.Send(request);

        Assert.That((int)response.StatusCode, Is.EqualTo(200));
        Assert.That(response.Content.ReadAsStringAsync().Result, Is.EqualTo("OK"));

        Assert.That(server.Handler.LatestRequestType, Is.EqualTo("GET"));
        Assert.That(server.Handler.LatestRequestPath, Is.EqualTo("/test"));
        Assert.That(server.Handler.LatestRequestBody, Is.Null);
    }

    [Test]
    public void PutRequestWithBody()
    {
        var server = HttpServerLifecycle.HttpServer;
        const string jsonData = "{\"name\":\"test\",\"value\":123}";
        using var client = new HttpClient();
        using var request = new HttpRequestMessage(HttpMethod.Put, server.Url + "/api/data")
        {
            Content = new StringContent(jsonData, Encoding.UTF8, "application/json"),
        };

        using var response = client.Send(request);

        Assert.That((int)response.StatusCode, Is.EqualTo(200));
        Assert.That(response.Content.ReadAsStringAsync().Result, Is.EqualTo("OK"));

        Assert.That(server.Handler.LatestRequestType, Is.EqualTo("PUT"));
        Assert.That(server.Handler.LatestRequestPath, Is.EqualTo("/api/data"));
        Assert.That(server.Handler.LatestRequestBody, Is.EqualTo(jsonData));
    }

    [Test]
    public void ErrorResponse()
    {
        var server = HttpServerLifecycle.HttpServer;
        server.Handler.SetResponse(404, "Not Found");
        using var client = new HttpClient();
        using var request = new HttpRequestMessage(HttpMethod.Get, server.Url + "/missing");

        using var response = client.Send(request);

        Assert.That((int)response.StatusCode, Is.EqualTo(404));
        Assert.That(response.Content.ReadAsStringAsync().Result, Is.EqualTo("Not Found"));

        Assert.That(server.Handler.LatestRequestType, Is.EqualTo("GET"));
        Assert.That(server.Handler.LatestRequestPath, Is.EqualTo("/missing"));
    }
}
