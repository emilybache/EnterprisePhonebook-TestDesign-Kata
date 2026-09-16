using NUnit.Framework.Interfaces;

namespace SammanCoaching.Phonebook.Tests.Testing;

/// <summary>
/// NUnit test action that starts a lightweight HTTP server before each test
/// and stops it afterward. Apply as [HttpServerLifecycle] on a test class,
/// then retrieve the running server via the static HttpServer property.
/// </summary>
[AttributeUsage(AttributeTargets.Class)]
public sealed class HttpServerLifecycle : Attribute, ITestAction
{
    private const string DefaultHost = "127.0.0.1";
    private const int DefaultPort = 9998;

    [ThreadStatic]
    private static LightweightHttpServer? _currentHttpServer;

    public void BeforeTest(ITest test)
    {
        var server = new LightweightHttpServer(DefaultHost, DefaultPort);
        server.Start();
        _currentHttpServer = server;
    }

    public void AfterTest(ITest test)
    {
        _currentHttpServer?.Stop();
        _currentHttpServer?.Handler.Reset();
        _currentHttpServer = null;
    }

    public ActionTargets Targets => ActionTargets.Test;

    public static LightweightHttpServer HttpServer =>
        _currentHttpServer ?? throw new InvalidOperationException(
            "No HTTP server is running; is the test class marked [HttpServerLifecycle]?");
}
