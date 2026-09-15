using SammanCoaching.Phonebook.Tests.Testing;

namespace SammanCoaching.Phonebook.Tests;

[HttpServerLifecycle]
public class SecurityClearanceAuthorizerTest
{
    [Test]
    public void Authorize()
    {
        var server = HttpServerLifecycle.HttpServer;
        var authorizer = new SecurityClearanceAuthorizer(server.Url);
        server.Handler.SetResponse(200, "OK");

        var result = authorizer.IsAuthorized();

        Assert.That(result, Is.True);
        Assert.That(server.Handler.LatestRequestType, Is.EqualTo("GET"));
    }

    [Test]
    public void NotAuthorize()
    {
        var server = HttpServerLifecycle.HttpServer;
        var authorizer = new SecurityClearanceAuthorizer(server.Url);
        server.Handler.SetResponse(404, "Not Found");

        var result = authorizer.IsAuthorized();

        Assert.That(result, Is.False);
        Assert.That(server.Handler.LatestRequestType, Is.EqualTo("GET"));
    }
}
