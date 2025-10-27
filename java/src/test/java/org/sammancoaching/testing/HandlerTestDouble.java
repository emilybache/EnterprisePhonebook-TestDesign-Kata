package org.sammancoaching.testing;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * HTTP handler test double that records request details and stubs responses.
 */
public class HandlerTestDouble implements HttpHandler {
    private volatile int responseCode = 200;
    private volatile String message = "OK";
    private volatile String latestRequestType = null;
    private volatile byte[] latestRequestBody = null;
    private volatile String latestRequestPath = null;

    public void reset() {
        responseCode = 200;
        message = "OK";
        latestRequestType = null;
        latestRequestBody = null;
        latestRequestPath = null;
    }

    public void setResponse(int responseCode, String message) {
        this.responseCode = responseCode;
        this.message = message;
    }

    public String getLatestRequestType() {
        return latestRequestType;
    }


    public String getLatestRequestBody() {
        if (latestRequestBody == null) {
            return null;
        }
        return new String(latestRequestBody, StandardCharsets.UTF_8);
    }

    public String getLatestRequestPath() {
        return latestRequestPath;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        latestRequestType = method;
        latestRequestPath = exchange.getRequestURI().getPath();

        // Read request body for PUT requests
        if ("PUT".equals(method) || "POST".equals(method)) {
            try (InputStream inputStream = exchange.getRequestBody()) {
                latestRequestBody = inputStream.readAllBytes();
            }
        }

        // Send response
        if (responseCode == 200) {
            byte[] responseBytes = message.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(responseCode, responseBytes.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(responseBytes);
            }
        } else {
            // Send error response
            byte[] responseBytes = message.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(responseCode, responseBytes.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(responseBytes);
            }
        }
    }
}
