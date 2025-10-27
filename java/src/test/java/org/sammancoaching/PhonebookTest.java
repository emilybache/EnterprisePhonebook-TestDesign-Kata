package org.sammancoaching;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PhonebookTest {

    private Phonebook phonebook;

    @BeforeEach
    void setUp() {
        phonebook = new Phonebook();
    }


    @Test
    void testSize() {
        assertEquals(0, phonebook.size());
        phonebook.add("Bob", "1234");
        assertEquals(1, phonebook.size());
    }

    @Test
    void testLookupMissingName() {
        assertThrows(RuntimeException.class, () -> phonebook.lookup("Bob"));
    }

    @Test
    void testLookUpByName() {
        phonebook.add("Bob", "1234");
        String result = phonebook.lookup("Bob");
        assertEquals("1234", result);
    }

    @Test
    void testLookUpByNameLongerNumber() {
        phonebook.add("Ann", "12345678");
        String result = phonebook.lookup("Ann");
        assertEquals("12345678", result);
    }

    @Test
    void testLookupByName() {
        phonebook.add("Bob", "12345");
        String number = phonebook.lookup("Bob");
        assertEquals("12345", number);
    }

    @Test
    void testNoClashes() {
        phonebook.add("Bob", "12345");
        phonebook.add("Sid", "6789");
        List<Map.Entry<String, String>> clashes = phonebook.findClashes("0987");
        assertTrue(clashes.isEmpty());
    }

    @Test
    void testClashIdentical() {
        phonebook.add("Bob", "12345");
        phonebook.add("Sid", "12346");
        List<Map.Entry<String, String>> clashes = phonebook.findClashes("12345");
        
        List<Map.Entry<String, String>> expected = List.of(
                Map.entry("Bob", "12345")
        );
        
        assertIterableEquals(expected, clashes);
    }

    @Test
    void testSeveralClashes() {
        phonebook.add("Bob", "12345");
        phonebook.add("Sid", "12346");
        List<Map.Entry<String, String>> clashes = phonebook.findClashes("1234");

        List<Map.Entry<String, String>> expected = List.of(
                Map.entry("Bob", "12345"),
                Map.entry("Sid", "12346")
        );

        assertIterableEquals(expected, clashes);
    }

    @Test
    void testSeveralClashesOtherWayAround() {
        phonebook.add("Bob", "1234");
        List<Map.Entry<String, String>> clashes = phonebook.findClashes("123456");

        List<Map.Entry<String, String>> expected = List.of(
                Map.entry("Bob", "1234")
        );

        assertIterableEquals(expected, clashes);
    }
}
