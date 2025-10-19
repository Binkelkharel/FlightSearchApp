// File: FlightSearchTest.java
// JUnit 5 tests for FlightSearch
// package flight;
package com.mycompany.flight;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This test suite covers the 11 conditions described in the assignment plus
 * a set of valid test cases. Each condition has at least two test datas where
 * appropriate and the valid case contains multiple variations.
 */
public class FlightSearchTest {
    private FlightSearch fs;
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/uuuu");

    @BeforeEach
    public void setup() {
        fs = new FlightSearch();
    }

    // helper to create future date strings
    private String futureDate(int daysFromToday) {
        return LocalDate.now().plusDays(daysFromToday).format(DTF);
    }

    // ---------- Condition 1: total passengers 1..9 ----------
    @Test
    public void testTotalPassengersTooFew() {
        boolean ok = fs.runFlightSearch(futureDate(1), "syd", false, futureDate(2), "mel", "economy", 0, 0, 0);
        assertFalse(ok);
    }

    @Test
    public void testTotalPassengersTooMany() {
        boolean ok = fs.runFlightSearch(futureDate(1), "syd", false, futureDate(2), "mel", "economy", 5, 3, 2); // total 10
        assertFalse(ok);
    }

    // ---------- Condition 2 & 10: children cannot be emergency or first class; only economy can have emergency ----------
    @Test
    public void testChildrenInEmergencyRowNotAllowed() {
        boolean ok = fs.runFlightSearch(futureDate(1), "syd", true, futureDate(2), "mel", "economy", 2, 1, 0);
        assertFalse(ok);
    }

    @Test
    public void testChildrenInFirstClassNotAllowed() {
        boolean ok = fs.runFlightSearch(futureDate(1), "syd", false, futureDate(2), "mel", "first", 2, 1, 0);
        assertFalse(ok);
    }

    // ---------- Condition 3: infants cannot be emergency or business ----------
    @Test
    public void testInfantsInEmergencyRowNotAllowed() {
        boolean ok = fs.runFlightSearch(futureDate(1), "syd", true, futureDate(2), "mel", "economy", 2, 0, 1);
        assertFalse(ok);
    }

    @Test
    public void testInfantsInBusinessNotAllowed() {
        boolean ok = fs.runFlightSearch(futureDate(1), "syd", false, futureDate(2), "mel", "business", 2, 0, 1);
        assertFalse(ok);
    }

    // ---------- Condition 4: children up to 2 per adult ----------
    @Test
    public void testTooManyChildrenForAdults() {
        // 1 adult -> max 2 children
        boolean ok = fs.runFlightSearch(futureDate(1), "syd", false, futureDate(2), "mel", "economy", 1, 3, 0);
        assertFalse(ok);
    }

    @Test
    public void testChildrenBoundaryAllowed() {
        // boundary: 2 adults -> 4 children allowed
        boolean ok = fs.runFlightSearch(futureDate(1), "syd", false, futureDate(2), "mel", "economy", 2, 4, 0);
        assertTrue(ok);
        // verify attributes set
        assertEquals(2, fs.getAdultPassengerCount());
        assertEquals(4, fs.getChildPassengerCount());
    }

    // ---------- Condition 5: infants one per adult ----------
    @Test
    public void testTooManyInfantsForAdults() {
        boolean ok = fs.runFlightSearch(futureDate(1), "syd", false, futureDate(2), "mel", "economy", 1, 0, 2);
        assertFalse(ok);
    }

    @Test
    public void testInfantBoundaryAllowed() {
        boolean ok = fs.runFlightSearch(futureDate(1), "syd", false, futureDate(2), "mel", "economy", 2, 0, 2);
        assertTrue(ok);
        assertEquals(2, fs.getInfantPassengerCount());
    }

    // ---------- Condition 6 & 7: departure date not in past and strict date validation ----------
    @Test
    public void testDepartureDateInPast() {
        String past = LocalDate.now().minusDays(1).format(DTF);
        boolean ok = fs.runFlightSearch(past, "syd", false, futureDate(1), "mel", "economy", 1, 0, 0);
        assertFalse(ok);
    }

    @Test
    public void testInvalidDateFormatOrNonexistentDate() {
        // 29/02/2025 is invalid (2025 not leap year)
        boolean ok = fs.runFlightSearch("29/02/2025", "syd", false, "01/03/2025", "mel", "economy", 1, 0, 0);
        assertFalse(ok);
    }

    // ---------- Condition 8: return cannot be before departure ----------
    @Test
    public void testReturnBeforeDeparture() {
        String dep = futureDate(5);
        String ret = futureDate(3);
        boolean ok = fs.runFlightSearch(dep, "syd", false, ret, "mel", "economy", 1, 0, 0);
        assertFalse(ok);
    }

    // ---------- Condition 9: seating class must be one of allowed ----------
    @Test
    public void testInvalidSeatingClass() {
        boolean ok = fs.runFlightSearch(futureDate(1), "syd", false, futureDate(2), "mel", "ultra economy", 1, 0, 0);
        assertFalse(ok);
    }

    // ---------- Condition 11: airport codes allowed and not equal ----------
    @Test
    public void testInvalidAirportCode() {
        boolean ok = fs.runFlightSearch(futureDate(1), "abc", false, futureDate(2), "mel", "economy", 1, 0, 0);
        assertFalse(ok);
    }

    @Test
    public void testSameDepartureAndDestinationNotAllowed() {
        boolean ok = fs.runFlightSearch(futureDate(1), "syd", false, futureDate(2), "syd", "economy", 1, 0, 0);
        assertFalse(ok);
    }

    // ---------- Valid cases: at least 4 variations ----------
    @Test
    public void testValidCaseSimpleEconomy() {
        boolean ok = fs.runFlightSearch(futureDate(1), "syd", false, futureDate(2), "mel", "economy", 1, 0, 0);
        assertTrue(ok);
        assertEquals("syd", fs.getDepartureAirportCode());
        assertEquals("mel", fs.getDestinationAirportCode());
        assertEquals("economy", fs.getSeatingClass());
    }

    @Test
    public void testValidCaseEconomyWithEmergencyFalse() {
        boolean ok = fs.runFlightSearch(futureDate(10), "mel", false, futureDate(11), "pvg", "economy", 2, 2, 1);
        assertTrue(ok);
        assertEquals(2, fs.getAdultPassengerCount());
        assertEquals(2, fs.getChildPassengerCount());
        assertEquals(1, fs.getInfantPassengerCount());
    }

    @Test
    public void testValidCasePremiumEconomyNoEmergency() {
        boolean ok = fs.runFlightSearch(futureDate(3), "doh", false, futureDate(5), "cdg", "premium economy", 2, 0, 0);
        assertTrue(ok);
        assertEquals("premium economy", fs.getSeatingClass());
    }

    @Test
    public void testValidCaseBusinessWithoutInfantOrEmergency() {
        boolean ok = fs.runFlightSearch(futureDate(4), "lax", false, futureDate(10), "del", "business", 1, 0, 0);
        assertTrue(ok);
        assertEquals("business", fs.getSeatingClass());
    }
}
