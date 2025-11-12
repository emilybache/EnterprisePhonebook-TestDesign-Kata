package org.sammancoaching.testing

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


/**
 * Unit test to check the LightweightHttpServer works correctly.
 * This test is not a good example of how to use this class in your own tests though.
 */
@ExtendWith(HttpServerLifecycle::class)
class LightweightHttpServerTest {
    @Test
    @Throws(Exception::class)
    fun testGetRequest() {
        val httpServer: LightweightHttpServer = HttpServerLifecycle.httpServer!!
        val response: HttpResponse<String?>
        HttpClient.newHttpClient().use { client ->

            // Make a GET request
            val request = HttpRequest.newBuilder()
                .uri(URI.create(httpServer.url + "/test"))
                .GET()
                .build()
            response = client.send<String?>(request, HttpResponse.BodyHandlers.ofString())
        }
        // Verify response
        Assertions.assertEquals(200, response.statusCode())
        Assertions.assertEquals("OK", response.body())

        // Verify handler recorded the request
        Assertions.assertEquals("GET", httpServer.getHandler().latestRequestType)
        Assertions.assertEquals("/test", httpServer.getHandler().latestRequestPath)
        Assertions.assertNull(httpServer.getHandler().getLatestRequestBody())
    }

    @Test
    @Throws(Exception::class)
    fun testPutRequestWithBody() {
        val httpServer: LightweightHttpServer = HttpServerLifecycle.httpServer!!
        val jsonData: String?
        val response: HttpResponse<String?>
        HttpClient.newHttpClient().use { client ->
            jsonData = "{\"name\":\"test\",\"value\":123}"
            // Make a PUT request with JSON body
            val request = HttpRequest.newBuilder()
                .uri(URI.create(httpServer.url + "/api/data"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonData))
                .build()
            response = client.send<String?>(request, HttpResponse.BodyHandlers.ofString())
        }
        // Verify response
        Assertions.assertEquals(200, response.statusCode())
        Assertions.assertEquals("OK", response.body())

        // Verify handler recorded the request details
        Assertions.assertEquals("PUT", httpServer.getHandler().latestRequestType)
        Assertions.assertEquals("/api/data", httpServer.getHandler().latestRequestPath)
        Assertions.assertEquals(jsonData, httpServer.getHandler().getLatestRequestBody())
    }

    @Test
    @Throws(Exception::class)
    fun testErrorResponse() {
        val httpServer: LightweightHttpServer = HttpServerLifecycle.httpServer!!
        val response: HttpResponse<String?>
        HttpClient.newHttpClient().use { client ->

            // Configure handler to return error
            httpServer.getHandler().setResponse(404, "Not Found")

            // Make a GET request
            val request = HttpRequest.newBuilder()
                .uri(URI.create(httpServer.url + "/missing"))
                .GET()
                .build()
            response = client.send<String?>(request, HttpResponse.BodyHandlers.ofString())
        }
        // Verify error response
        Assertions.assertEquals(404, response.statusCode())
        Assertions.assertEquals("Not Found", response.body())

        // Verify handler recorded the request
        Assertions.assertEquals("GET", httpServer.getHandler().latestRequestType)
        Assertions.assertEquals("/missing", httpServer.getHandler().latestRequestPath)
    }
}