using System.Net;

namespace SammanCoaching.Phonebook;

public sealed class SecurityClearanceAuthorizer : IAuthorizer
{
    private readonly string _url;
    private readonly HttpClient _httpClient;

    public SecurityClearanceAuthorizer(string url)
    {
        _url = url;
        _httpClient = new HttpClient();
    }

    public async Task<bool> IsAuthorizedAsync()
    {
        try
        {
            using var request = new HttpRequestMessage(HttpMethod.Get, _url + "/authenticate");
            using var response = await _httpClient.SendAsync(request);

            // BUG: should be response.StatusCode == HttpStatusCode.OK
            return response.StatusCode != HttpStatusCode.Forbidden;
        }
        catch (HttpRequestException)
        {
            return false;
        }
    }
}
