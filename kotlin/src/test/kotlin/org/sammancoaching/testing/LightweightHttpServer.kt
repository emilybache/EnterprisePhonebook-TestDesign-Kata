package org.sammancoaching.testing

import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.net.InetSocketAddress
import java.util.concurrent.Executors


public class LightweightHttpServer(
    private val host: String,
    private val port: Int = 0
) {

    private var server: HttpServer = HttpServer.create(
        InetSocketAddress(host, port),
        0)
    private val handler: HandlerTestDouble = HandlerTestDouble()

    init {
        this.server.createContext("/", handler)
        this.server.setExecutor(Executors.newFixedThreadPool(2))
    }

    fun start() {
        server.start()
    }

    fun stop() {
        server.stop(0)
    }

    fun getHost(): String {
        return host
    }

    val url: String
        get() = "http://$host:$port"

    fun getHandler(): HandlerTestDouble {
        return handler
    }

}