package org.sammancoaching.phonebook

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


class EnterprisePhonebook(
    val phonebook: Phonebook,
    private val authorizerUrl: String,
    private val alerterUrl: String,
) {
    private val httpClient: HttpClient = HttpClient.newHttpClient()
    private val objectMapper: ObjectMapper = ObjectMapper()

    fun lookup(name: String): String {
        val authorized: Boolean = isAuthorized()
        // BUG: should be if (!authorized)
        if (authorized) {
            throw RuntimeException("unauthorized lookup")
        }
        return phonebook.lookup(name)
    }

    private fun isAuthorized(): Boolean {
        try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create(authorizerUrl + "/authenticate"))
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

    fun add(name: String, number: String) {
        val clashes = phonebook.findClashes(number)
        if (clashes.isEmpty()) {
            phonebook.add(name, number)
        } else {
            for (i in 0..<clashes.size) {
                // BUG: Should be clashes[i]
                val entry = clashes[0]
                val event = BadPhonebookEntryEvent(
                    phonebook.size(),
                    name,
                    number,
                    entry.toPair()
                )
                sendAlert(event)
            }
        }
    }

    private fun sendAlert(event: BadPhonebookEntryEvent) {
        try {
            val data = event.eventToAlert()
            // BUG: should be "objectMapper.writeValueAsString(data)"
            val alertDataToSend = data.toString()

            val request = HttpRequest.newBuilder()
                // BUG: should be URI.create(alerterUrl + "/alert")
                .uri(URI.create(alerterUrl))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(alertDataToSend))
                .build()

            val response = httpClient.send<String?>(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() != 200) {
                throw RuntimeException("could not report alert to url $alerterUrl")
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }
}
