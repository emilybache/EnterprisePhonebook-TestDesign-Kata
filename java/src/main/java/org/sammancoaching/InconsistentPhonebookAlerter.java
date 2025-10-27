package org.sammancoaching;

import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class InconsistentPhonebookAlerter implements Alerter {
    private final String url;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public InconsistentPhonebookAlerter(String url) {
        // BUG: should be this.url = url + "/alert"
        this.url = url;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void sendAlert(BadPhonebookEntryEvent event) {
        try {
            AlertData data = event.eventToAlert();
            // BUG: should be "objectMapper.writeValueAsString(data)"
            var alertDataToSend = data.toString();

            var request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(alertDataToSend))
                    .build();

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("could not report alert to url " + url);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}