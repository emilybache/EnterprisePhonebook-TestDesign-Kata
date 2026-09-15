using System.Net;

namespace SammanCoaching.Phonebook.Tests.Testing;

/// <summary>
/// A lightweight HTTP server for testing purposes, similar to Python's ThreadingHTTPServer.
/// Uses a HandlerTestDouble that records requests for verification in tests.
/// </summary>
public sealed class LightweightHttpServer
{
    private readonly HttpListener _listener;
    private readonly Thread _listenerThread;
    private volatile bool _running;

    public string Host { get; }
    public int Port { get; }
    public HandlerTestDouble Handler { get; } = new();

    public string Url => $"http://{Host}:{Port}";

    public LightweightHttpServer(string host, int port)
    {
        Host = host;
        Port = port;
        _listener = new HttpListener();
        _listener.Prefixes.Add($"http://{host}:{port}/");
        _listenerThread = new Thread(Listen) { IsBackground = true };
    }

    public void Start()
    {
        _running = true;
        _listener.Start();
        _listenerThread.Start();
    }

    public void Stop()
    {
        _running = false;
        _listener.Stop();
        _listenerThread.Join();
        _listener.Close();
    }

    private void Listen()
    {
        while (_running)
        {
            HttpListenerContext context;
            try
            {
                context = _listener.GetContext();
            }
            catch (Exception) when (!_running)
            {
                // Listener was stopped while waiting for a request; exit quietly.
                break;
            }

            Handler.Handle(context);
        }
    }
}
