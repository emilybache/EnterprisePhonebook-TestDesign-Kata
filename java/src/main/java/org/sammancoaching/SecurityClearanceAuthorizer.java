package org.sammancoaching;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SecurityClearanceAuthorizer implements Authorizer {
    private final String url;
    private final HttpClient httpClient;

    public SecurityClearanceAuthorizer(String url) {
        this.url = url;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public boolean isAuthorized() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/authenticate"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // BUG: should be "response.statusCode() == 200"
            return response.statusCode() != 403;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}