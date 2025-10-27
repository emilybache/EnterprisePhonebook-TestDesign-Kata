package org.sammancoaching.testing;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 extension for managing an HttpServer lifecycle in tests.
 * Starts a lightweight HTTP server before the test
 * and stores a reference to it in a thread local variable where the test can retrieve it.
 * Stops the server after the test.
 */
public class HttpServerLifecycle implements BeforeEachCallback, AfterEachCallback {

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 9998;
    
    private static LightweightHttpServer currentHttpServer;
    
    private final String host;
    private final int port;
    
    public HttpServerLifecycle() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }
    
    public HttpServerLifecycle(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        LightweightHttpServer server = new LightweightHttpServer(host, port);
        server.start();

        currentHttpServer = server;
    }
    
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (currentHttpServer != null) {
            currentHttpServer.stop();
            currentHttpServer.getHandler().reset();
        }
        currentHttpServer = null;
    }
    
    public static LightweightHttpServer getHttpServer() {
        return currentHttpServer;
    }

}