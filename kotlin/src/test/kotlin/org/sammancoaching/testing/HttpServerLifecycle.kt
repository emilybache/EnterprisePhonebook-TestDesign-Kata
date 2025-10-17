package org.sammancoaching.testing

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext


/**
 * JUnit 5 extension for managing an HttpServer lifecycle in tests.
 * Starts a lightweight HTTP server before the test
 * and stores a reference to it in a field where the test can retrieve it.
 * Stops the server after the test.
 */
class HttpServerLifecycle @JvmOverloads constructor(
    private val host: String = DEFAULT_HOST,
    private val port: Int = DEFAULT_PORT
) : BeforeEachCallback, AfterEachCallback {
    @Throws(Exception::class)
    override fun beforeEach(context: ExtensionContext?) {
        val server = LightweightHttpServer(host, port)
        server.start()

        httpServer = server
    }

    @Throws(Exception::class)
    override fun afterEach(context: ExtensionContext?) {
        if (httpServer != null) {
            httpServer!!.stop()
            httpServer!!.getHandler().reset()
        }
        httpServer = null
    }

    companion object {
        private const val DEFAULT_HOST = "127.0.0.1"
        private const val DEFAULT_PORT = 9998

        var httpServer: LightweightHttpServer? = null
            private set
    }
}