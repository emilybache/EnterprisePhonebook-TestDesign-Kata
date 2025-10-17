package org.sammancoaching.phonebook

import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class SecurityClearanceAuthorizer(private val url: String) : Authorizer {
    private val httpClient: HttpClient = HttpClient.newHttpClient()

    override val isAuthorized: Boolean
        get() {
            try {
                val request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/authenticate"))
                    .GET()
                    .build()

                val response = httpClient.send<String?>(request, HttpResponse.BodyHandlers.ofString())

                // BUG: should be "response.statusCode() == 200"
                return response.statusCode() != 403
            } catch (e: IOException) {
                return false
            } catch (e: InterruptedException) {
                return false
            }
        }
}