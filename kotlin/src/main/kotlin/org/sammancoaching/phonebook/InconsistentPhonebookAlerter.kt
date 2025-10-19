package org.sammancoaching.phonebook

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class InconsistentPhonebookAlerter(
    private val url: String
) : Alerter {
    private val httpClient: HttpClient = HttpClient.newHttpClient()
    private val objectMapper: ObjectMapper = ObjectMapper()

    override fun sendAlert(event: BadPhonebookEntryEvent) {
        try {
            val data = event.eventToAlert()
            // BUG: should be "objectMapper.writeValueAsString(data)"
            val alertDataToSend  = data.toString()

            val request = HttpRequest.newBuilder()
                // BUG: should be this.url = url + "/alert"
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(alertDataToSend))
                .build()

            val response = httpClient.send<String?>(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() != 200) {
                throw RuntimeException("could not report alert to url $url")
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }
}