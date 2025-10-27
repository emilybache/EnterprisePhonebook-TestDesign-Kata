package org.sammancoaching;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EnterprisePhonebookTest {

    private Phonebook phonebook;

    @Mock
    private Authorizer mockAuthorizer;

    @Mock
    private Alerter mockAlerter;


    private EnterprisePhonebook enterprisePhonebook;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        phonebook = new Phonebook();
        enterprisePhonebook = new EnterprisePhonebook(phonebook, mockAuthorizer, mockAlerter);
    }

    @Test
    void testLookupAuthorized() {
        phonebook.add("Bob", "1234");
        when(mockAuthorizer.isAuthorized()).thenReturn(true);

        String result = enterprisePhonebook.lookup("Bob");

        assertEquals("1234", result);
    }

    @Test
    void testLookupNotAuthorized() {
        phonebook.add("Bob", "1234");
        when(mockAuthorizer.isAuthorized()).thenReturn(false);

        assertThrows(RuntimeException.class, () -> enterprisePhonebook.lookup("Bob"));
    }

    @Test
    void testAlertInconsistentEntryAttempts() {
        enterprisePhonebook.add("Bob", "12345");
        enterprisePhonebook.add("Sid", "12346");
        enterprisePhonebook.add("Ted", "1234");

        BadPhonebookEntryEvent expectedCall1 = new BadPhonebookEntryEvent(2, "Ted", "1234", Map.entry("Bob", "12345"));
        BadPhonebookEntryEvent expectedCall2 = new BadPhonebookEntryEvent(2, "Ted", "1234", Map.entry("Sid", "12346"));

        verify(mockAlerter).sendAlert(expectedCall1);
        verify(mockAlerter).sendAlert(expectedCall2);
    }

    @Test
    void testDoNotAddInconsistentEntry() {
        enterprisePhonebook.add("Bob", "12345");
        enterprisePhonebook.add("Sid", "12346");
        enterprisePhonebook.add("Ted", "1234");

        // Verify Ted was not added due to clashes
        assertThrows(RuntimeException.class, () -> enterprisePhonebook.getPhonebook().lookup("Ted"));

        // Verify only Bob and Sid are in the phonebook
        assertEquals(2, enterprisePhonebook.getPhonebook().size());
    }


}
