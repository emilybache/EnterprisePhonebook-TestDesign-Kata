package org.sammancoaching.testing

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import java.io.IOException
import java.nio.charset.StandardCharsets
import kotlin.concurrent.Volatile


/**
 * HTTP handler test double that records request details and stubs responses.
 */
class HandlerTestDouble : HttpHandler {
    @Volatile
    private var responseCode = 200

    @Volatile
    private var message = "OK"

    @Volatile
    var latestRequestType: String? = null
        private set

    @Volatile
    private var latestRequestBody: ByteArray? = null

    @Volatile
    var latestRequestPath: String? = null
        private set

    fun reset() {
        responseCode = 200
        message = "OK"
        latestRequestType = null
        latestRequestBody = null
        latestRequestPath = null
    }

    fun setResponse(responseCode: Int, message: String) {
        this.responseCode = responseCode
        this.message = message
    }


    fun getLatestRequestBody(): String? {
        if (latestRequestBody == null) {
            return null
        }
        return kotlin.text.String(latestRequestBody!!, StandardCharsets.UTF_8)
    }

    @Throws(IOException::class)
    override fun handle(exchange: HttpExchange) {
        val method = exchange.getRequestMethod()
        latestRequestType = method
        latestRequestPath = exchange.getRequestURI().getPath()

        // Read request body for PUT requests
        if ("PUT" == method || "POST" == method) {
            exchange.getRequestBody().use { inputStream ->
                latestRequestBody = inputStream.readAllBytes()
            }
        }

        // Send response
        if (responseCode == 200) {
            val responseBytes = message.toByteArray(StandardCharsets.UTF_8)
            exchange.sendResponseHeaders(responseCode, responseBytes.size.toLong())
            exchange.getResponseBody().use { outputStream ->
                outputStream.write(responseBytes)
            }
        } else {
            // Send error response
            val responseBytes = message.toByteArray(StandardCharsets.UTF_8)
            exchange.sendResponseHeaders(responseCode, responseBytes.size.toLong())
            exchange.getResponseBody().use { outputStream ->
                outputStream.write(responseBytes)
            }
        }
    }
}
