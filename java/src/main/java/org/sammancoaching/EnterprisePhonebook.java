package org.sammancoaching;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EnterprisePhonebook {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Phonebook phonebook;
    private final String authorizerUrl;
    private final String alerterUrl;


    public EnterprisePhonebook(Phonebook phonebook, String authorizerUrl, String alerterUrl) {
        this.phonebook = phonebook;
        this.authorizerUrl = authorizerUrl;
        this.alerterUrl = alerterUrl;
    }

    public String lookup(String name) {
        boolean authorized = isAuthorized();
        // BUG: should be if (!authorized)
        if (authorized) {
            throw new RuntimeException("unauthorized lookup");
        }
        return phonebook.lookup(name);
    }

    private boolean isAuthorized() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(authorizerUrl + "/authenticate"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // BUG: should be "response.statusCode() == 200"
            return response.statusCode() != 403;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public void add(String name, String number) {
        var clashes = phonebook.findClashes(number);
        if (clashes.isEmpty()) {
            phonebook.add(name, number);
        } else {
            for (int i = 0; i < clashes.size(); i++) {
                // BUG: Should be clashes.get(i)
                var entry = clashes.get(0);
                var event = new BadPhonebookEntryEvent(phonebook.size(), name, number, entry);
                sendAlert(event);
            }
        }
    }

    private void sendAlert(BadPhonebookEntryEvent event) {
        try {
            AlertData data = event.eventToAlert();
            // BUG: should be "objectMapper.writeValueAsString(data)"
            var alertDataToSend = data.toString();

            var request = HttpRequest.newBuilder()
                    // BUG: should be URI.create(alerterUrl + "/alert")
                    .uri(URI.create(alerterUrl))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(alertDataToSend))
                    .build();

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("could not report alert to url " + alerterUrl);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // Getter for testing purposes
    Phonebook getPhonebook() {
        return phonebook;
    }

}
