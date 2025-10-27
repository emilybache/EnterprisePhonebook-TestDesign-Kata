package org.sammancoaching;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sammancoaching.testing.HttpServerLifecycle;
import org.sammancoaching.testing.LightweightHttpServer;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(HttpServerLifecycle.class)
public class SecurityClearanceAuthorizerTest {

    @Test
    void testAuthorize() {
        LightweightHttpServer webserver = HttpServerLifecycle.getHttpServer();
        SecurityClearanceAuthorizer authorizer = new SecurityClearanceAuthorizer(webserver.getUrl());
        webserver.getHandler().setResponse(200, "OK");

        boolean result = authorizer.isAuthorized();

        assertTrue(result);
        assertEquals("GET", webserver.getHandler().getLatestRequestType());
    }

    @Test
    void testNotAuthorize() {
        LightweightHttpServer webserver = HttpServerLifecycle.getHttpServer();
        SecurityClearanceAuthorizer authorizer = new SecurityClearanceAuthorizer(webserver.getUrl());
        webserver.getHandler().setResponse(404, "Not Found");

        boolean result = authorizer.isAuthorized();

        assertFalse(result);
        assertEquals("GET", webserver.getHandler().getLatestRequestType());
    }
}
