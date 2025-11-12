Enterprise Phonebook Test Design Kata - Java version
====================================================

For exercise instructions see [top level README](../README.md)

Test Fixtures
-------------
This project includes some test fixtures to help you to test code that interacts with a webserver.
There are some test fixtures supplied in the `testing` package. The `HttpServerLifecycle` annotation class will start automatically before the test and be shut down afterwards. From that class you can get hold of the url of this webserver. You can configure the responses it gives using the `HandlerTestDouble` class. You can also query this test double to find out details about the most recent call to the webserver.

The example below shows how to use these fixtures to control the response of the webserver and check the request that was received by it.

```java
@ExtendWith(HttpServerLifecycle.class)
public class Sample {
    @Test
    void example() throws IOException, InterruptedException {
        var server = HttpServerLifecycle.getHttpServer();
        server.getHandler().setResponse(404, "Not Found");

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(server.getUrl() + "/path"))
            .GET()
            .build();
        
        var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
        assertEquals("/path", server.getHandler().getLatestRequestPath());
    }
}
```
