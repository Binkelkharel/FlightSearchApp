// File: FlightSearch.java
// package flight;
package com.mycompany.flight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of FlightSearch.runFlightSearch and getters so unit tests can
 * verify attribute initialization.
 *
 * Notes:
 * - All string inputs are expected to be lowercase (per assignment brief).
 * - Date format strictly DD/MM/YYYY and validated using java.time with STRICT resolver.
 */
public class FlightSearch {
    private String  departureDate;
    private String  departureAirportCode;
    private boolean emergencyRowSeating;
    private String  returnDate;
    private String  destinationAirportCode;
    private String  seatingClass;
    private int     adultPassengerCount;
    private int     childPassengerCount;
    private int     infantPassengerCount;

    // Keep a copy of defaults so tests can assert "no change" on failed validation
    private static final String[] ALLOWED_AIRPORTS_ARRAY = {"syd","mel","lax","cdg","del","pvg","doh"};
    private static final Set<String> ALLOWED_AIRPORTS = new HashSet<>(Arrays.asList(ALLOWED_AIRPORTS_ARRAY));
    private static final String[] ALLOWED_CLASSES = {"economy","premium economy","business","first"};

    // Date formatter for strict dd/MM/uuuu parsing
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    public FlightSearch() {
        // fields default to null/false/0 automatically
    }

    /**
     * Validates provided search parameters. If valid, initializes the object attributes
     * and returns true. If invalid, object state is left unchanged and the method
     * returns false.
     */
    public boolean runFlightSearch(String departureDate,    String departureAirportCode,   boolean emergencyRowSeating,
                                   String returnDate,       String destinationAirportCode, String seatingClass,
                                   int adultPassengerCount, int childPassengerCount,       int infantPassengerCount) {
        // Local copies used for validation - only commit to fields after all checks pass
        try {
            // Null checks
            if (departureDate == null || returnDate == null || departureAirportCode == null || destinationAirportCode == null || seatingClass == null) {
                return false;
            }

            // Condition 1: total passengers 1..9
            int totalPassengers = adultPassengerCount + childPassengerCount + infantPassengerCount;
            if (totalPassengers < 1 || totalPassengers > 9) return false;

            // Condition 9: seating class must be one of allowed
            boolean classOk = Arrays.asList(ALLOWED_CLASSES).contains(seatingClass);
            if (!classOk) return false;

            // Condition 11: airport codes allowed and not same
            if (!ALLOWED_AIRPORTS.contains(departureAirportCode) || !ALLOWED_AIRPORTS.contains(destinationAirportCode)) return false;
            if (departureAirportCode.equals(destinationAirportCode)) return false;

            // Condition 10: Only economy can have emergency row
            if (emergencyRowSeating && !"economy".equals(seatingClass)) return false;

            // Condition 2 & 3: children/infants cannot be in emergency row or certain classes
            if (childPassengerCount > 0) {
                if (emergencyRowSeating) return false; // children cannot be in emergency row at all
                if ("first".equals(seatingClass)) return false; // children cannot be in first class
            }
            if (infantPassengerCount > 0) {
                if (emergencyRowSeating) return false; // infants cannot be in emergency row
                if ("business".equals(seatingClass)) return false; // infants cannot be in business class
            }

            // Condition 4: children must be seated next to an adult (up to 2 children per adult)
            if (childPassengerCount > adultPassengerCount * 2) return false;

            // Condition 5: one infant per adult
            if (infantPassengerCount > adultPassengerCount) return false;

            // Condition 7: strict date format and valid dates
            LocalDate depDate;
            LocalDate retDate;
            try {
                depDate = LocalDate.parse(departureDate, DTF);
                retDate = LocalDate.parse(returnDate, DTF);
            } catch (DateTimeParseException ex) {
                return false; // invalid format or invalid date like 29/02 on non-leap year
            }

            // Condition 6: departure date cannot be in the past (based on current system date)
            LocalDate today = LocalDate.now();
            if (depDate.isBefore(today)) return false;

            // Condition 8: return date cannot be before departure date (flights are two way only)
            if (retDate.isBefore(depDate)) return false;

            // All checks passed - commit to object state
            this.departureDate = departureDate;
            this.departureAirportCode = departureAirportCode;
            this.emergencyRowSeating = emergencyRowSeating;
            this.returnDate = returnDate;
            this.destinationAirportCode = destinationAirportCode;
            this.seatingClass = seatingClass;
            this.adultPassengerCount = adultPassengerCount;
            this.childPassengerCount = childPassengerCount;
            this.infantPassengerCount = infantPassengerCount;

            return true;
        } catch (Exception ex) {
            // Defensive: any unexpected exception -> validation fail
            return false;
        }
    }

    // Getters for unit tests to verify internal state
    public String getDepartureDate() { return departureDate; }
    public String getDepartureAirportCode() { return departureAirportCode; }
    public boolean isEmergencyRowSeating() { return emergencyRowSeating; }
    public String getReturnDate() { return returnDate; }
    public String getDestinationAirportCode() { return destinationAirportCode; }
    public String getSeatingClass() { return seatingClass; }
    public int getAdultPassengerCount() { return adultPassengerCount; }
    public int getChildPassengerCount() { return childPassengerCount; }
    public int getInfantPassengerCount() { return infantPassengerCount; }
}
