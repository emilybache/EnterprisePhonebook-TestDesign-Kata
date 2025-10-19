package org.sammancoaching.phonebook

import org.junit.jupiter.api.extension.ExtendWith
import org.sammancoaching.testing.HttpServerLifecycle
import org.sammancoaching.testing.LightweightHttpServer
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(HttpServerLifecycle::class)
class SecurityClearanceAuthorizerTest {
    @Test
    fun whenRemoteServiceReturns200_isAuthorized() {
        val server = HttpServerLifecycle.httpServer!!
        val authorizer = SecurityClearanceAuthorizer(server.url)

        val isAuthorized = authorizer.isAuthorized

        assertEquals(true, isAuthorized)
    }

    @Test
    fun whenRemoteServiceReturns404_isNotAuthorized() {
        val server = HttpServerLifecycle.httpServer!!
        server.getHandler().setResponse(404, "Not found")
        val authorizer = SecurityClearanceAuthorizer(server.url)

        val isAuthorized = authorizer.isAuthorized

        assertEquals(false, isAuthorized)
    }
}