EnterprisePhonebook in C#
=========================

For exercise instructions see [top level README](../README.md)

run tests:

    dotnet test

Test Fixtures
-------------
This project includes some test fixtures to help you to test code that interacts with a webserver.
There are some test fixtures supplied in the `Testing` folder. The `HttpServerLifecycle` test attribute will start a server automatically before the test and shut it down afterwards. From that class you can get hold of the url of this webserver. You can configure the responses it gives using the `HandlerTestDouble` class. You can also query this test double to find out details about the most recent call to the webserver.

The example below shows how to use these fixtures to control the response of the webserver and check the request that was received by it.

```csharp
[HttpServerLifecycle]
public class SampleTest
{
    [Test]
    public async Task Example()
    {
        var server = HttpServerLifecycle.HttpServer;
        server.Handler.SetResponse(404, "Not Found");

        using var client = new HttpClient();
        using var request = new HttpRequestMessage(HttpMethod.Get, server.Url + "/path");
        using var response = await client.SendAsync(request);

        Assert.That((int)response.StatusCode, Is.EqualTo(404));
        Assert.That(server.Handler.LatestRequestPath, Is.EqualTo("/path"));
    }
}
```
