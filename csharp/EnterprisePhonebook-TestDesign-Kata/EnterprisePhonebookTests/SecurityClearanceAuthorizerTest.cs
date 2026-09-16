using SammanCoaching.Phonebook.Tests.Testing;

namespace SammanCoaching.Phonebook.Tests;

[HttpServerLifecycle]
public class SecurityClearanceAuthorizerTest
{
    [Test]
    public async Task Authorize()
    {
        var server = HttpServerLifecycle.HttpServer;
        var authorizer = new SecurityClearanceAuthorizer(server.Url);
        server.Handler.SetResponse(200, "OK");

        var result = await authorizer.IsAuthorizedAsync();

        Assert.That(result, Is.True);
        Assert.That(server.Handler.LatestRequestType, Is.EqualTo("GET"));
    }

    [Test]
    public async Task NotAuthorize()
    {
        var server = HttpServerLifecycle.HttpServer;
        var authorizer = new SecurityClearanceAuthorizer(server.Url);
        server.Handler.SetResponse(404, "Not Found");

        var result = await authorizer.IsAuthorizedAsync();

        Assert.That(result, Is.False);
        Assert.That(server.Handler.LatestRequestType, Is.EqualTo("GET"));
    }
}
