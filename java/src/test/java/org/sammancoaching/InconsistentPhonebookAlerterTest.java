package org.sammancoaching;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sammancoaching.testing.HttpServerLifecycle;
import org.sammancoaching.testing.LightweightHttpServer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(HttpServerLifecycle.class)
public class InconsistentPhonebookAlerterTest {

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    void testEventToJsonData() {
        BadPhonebookEntryEvent event = new BadPhonebookEntryEvent(10, "Ted", "1234", Map.entry("Bob", "1234"));
        AlertData data = event.eventToAlert();
        
        assertEquals("10", data.size());
        assertEquals("Ted cannot be added with number 1234", data.newEntry());
        assertEquals("Bob with number 1234", data.clash());
    }

    @Test
    void testGenerateEvent() throws Exception {
        LightweightHttpServer httpServer = HttpServerLifecycle.getHttpServer();
        InconsistentPhonebookAlerter alerter = new InconsistentPhonebookAlerter(httpServer.getUrl());
        BadPhonebookEntryEvent event = new BadPhonebookEntryEvent(10, "Ted", "1234", Map.entry("Bob", "1234"));

        alerter.sendAlert(event);

        assertEquals("PUT", httpServer.getHandler().getLatestRequestType());
        String receivedDataAsString = httpServer.getHandler().getLatestRequestBody();
        AlertData receivedData = mapper.readValue(receivedDataAsString, AlertData.class);
        assertEquals(event.eventToAlert(), receivedData);
    }
}
