using System.Net;
using System.Text;

namespace SammanCoaching.Phonebook.Tests.Testing;

/// <summary>
/// HTTP handler test double that records request details and stubs responses.
/// </summary>
public sealed class HandlerTestDouble
{
    private readonly object _lock = new();
    private int _responseCode = 200;
    private string _message = "OK";

    public string? LatestRequestType { get; private set; }
    public string? LatestRequestPath { get; private set; }
    private byte[]? LatestRequestBodyBytes { get; set; }

    public string? LatestRequestBody =>
        LatestRequestBodyBytes is null ? null : Encoding.UTF8.GetString(LatestRequestBodyBytes);

    public void SetResponse(int responseCode, string message)
    {
        lock (_lock)
        {
            _responseCode = responseCode;
            _message = message;
        }
    }

    public void Reset()
    {
        lock (_lock)
        {
            _responseCode = 200;
            _message = "OK";
        }

        LatestRequestType = null;
        LatestRequestPath = null;
        LatestRequestBodyBytes = null;
    }

    public void Handle(HttpListenerContext context)
    {
        var request = context.Request;
        var response = context.Response;

        LatestRequestType = request.HttpMethod;
        LatestRequestPath = request.Url?.AbsolutePath;

        if (request.HttpMethod is "PUT" or "POST")
        {
            using var bodyStream = new MemoryStream();
            request.InputStream.CopyTo(bodyStream);
            LatestRequestBodyBytes = bodyStream.ToArray();
        }

        int responseCode;
        string message;
        lock (_lock)
        {
            responseCode = _responseCode;
            message = _message;
        }

        var responseBytes = Encoding.UTF8.GetBytes(message);
        response.StatusCode = responseCode;
        response.ContentLength64 = responseBytes.Length;
        response.OutputStream.Write(responseBytes, 0, responseBytes.Length);
        response.OutputStream.Close();
    }
}
