package org.sammancoaching.testing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(HttpServerLifecycle.class)
public class LightweightHttpServerTest {

    @Test
    public void testGetRequest() throws Exception {
        LightweightHttpServer httpServer = HttpServerLifecycle.getHttpServer();
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {

            // Make a GET request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(httpServer.getUrl() + "/test"))
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        // Verify response
        assertEquals(200, response.statusCode());
        assertEquals("OK", response.body());

        // Verify handler recorded the request
        assertEquals("GET", httpServer.getHandler().getLatestRequestType());
        assertEquals("/test", httpServer.getHandler().getLatestRequestPath());
        assertNull(httpServer.getHandler().getLatestRequestBody());
    }

    @Test
    public void testPutRequestWithBody() throws Exception {
        LightweightHttpServer httpServer = HttpServerLifecycle.getHttpServer();
        String jsonData;
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {

            jsonData = "{\"name\":\"test\",\"value\":123}";

            // Make a PUT request with JSON body
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(httpServer.getUrl() + "/api/data"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonData))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        // Verify response
        assertEquals(200, response.statusCode());
        assertEquals("OK", response.body());

        // Verify handler recorded the request details
        assertEquals("PUT", httpServer.getHandler().getLatestRequestType());
        assertEquals("/api/data", httpServer.getHandler().getLatestRequestPath());
        assertEquals(jsonData, httpServer.getHandler().getLatestRequestBody());
    }

    @Test
    public void testErrorResponse() throws Exception {
        LightweightHttpServer httpServer = HttpServerLifecycle.getHttpServer();
        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {

            // Configure handler to return error
            httpServer.getHandler().setResponse(404, "Not Found");

            // Make a GET request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(httpServer.getUrl() + "/missing"))
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        // Verify error response
        assertEquals(404, response.statusCode());
        assertEquals("Not Found", response.body());

        // Verify handler recorded the request
        assertEquals("GET", httpServer.getHandler().getLatestRequestType());
        assertEquals("/missing", httpServer.getHandler().getLatestRequestPath());
    }

}