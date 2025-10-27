package org.sammancoaching.testing;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * A lightweight HTTP server for testing purposes, similar to Python's ThreadingHTTPServer.
 * Uses a HandlerTestDouble that records requests for verification in tests.
 */
public class LightweightHttpServer {
    private final HttpServer server;
    private final HandlerTestDouble handler;
    private final String host;
    private final int port;

    public LightweightHttpServer(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.handler = new HandlerTestDouble();
        this.server = HttpServer.create(new InetSocketAddress(host, port), 0);
        this.server.createContext("/", handler);
        this.server.setExecutor(Executors.newFixedThreadPool(2));
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUrl() {
        return "http://" + host + ":" + port;
    }

    public HandlerTestDouble getHandler() {
        return handler;
    }

}