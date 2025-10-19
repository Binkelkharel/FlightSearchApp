Flightsearch.java



// package com.mycompany.flight;

// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.time.format.DateTimeParseException;
// import java.time.format.ResolverStyle;
// import java.util.Set;
// import java.util.Arrays;
// import java.util.HashSet;

// public class FlightSearch {
//    private String  departureDate;
//    private String  departureAirportCode;
//    private boolean emergencyRowSeating;
//    private String  returnDate;
//    private String  destinationAirportCode; 
//    private String  seatingClass;
//    private int     adultPassengerCount;
//    private int     childPassengerCount;
//    private int     infantPassengerCount;

//    // Define required constants for validation
//    private static final LocalDate CURRENT_DATE = LocalDate.of(2025, 10, 20); // Used for TC8, Assumed Current Date: 20/10/2025
//    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy").withResolverStyle(ResolverStyle.STRICT);
//    private static final Set<String> VALID_AIRPORT_CODES = new HashSet<>(Arrays.asList("SYD", "MEL", "LAX", "PVG", "CDG", "DOH"));
   
//    // NOTE: Using core terms for easier validation after normalization (removing " Class")
//    private static final Set<String> VALID_SEATING_CLASSES = new HashSet<>(Arrays.asList("economy", "premium economy", "business", "first"));

//    /**
//     * Runs a flight search after validating all input parameters based on business rules.
//     * If valid, initializes the class attributes and returns true. Otherwise, returns false.
//     *
//     * @param departureDate The desired departure date (e.g., "25/10/2025").
//     * @param departureAirportCode The 3-letter IATA code for departure (e.g., "SYD").
//     * @param emergencyRowSeating True if emergency row seating is requested.
//     * @param returnDate The desired return date (e.g., "30/10/2025").
//     * @param destinationAirportCode The 3-letter IATA code for destination (e.g., "LAX").
//     * @param seatingClass The seating class (e.g., "Economy", "First Class").
//     * @param adultPassengerCount Number of adults (16+).
//     * @param childPassengerCount Number of children (2-15).
//     * @param infantPassengerCount Number of infants (under 2).
//     * @return true if the search criteria are valid, false otherwise.
//     */
//    public boolean runFlightSearch(String departureDate,    String departureAirportCode,   boolean emergencyRowSeating, 
//                                   String returnDate,       String destinationAirportCode, String seatingClass, 
//                                   int adultPassengerCount, int childPassengerCount,       int infantPassengerCount) {
      
//       // --- 1. Passenger Count Validation (TC2, TC3) ---
//       if (adultPassengerCount < 0 || childPassengerCount < 0 || infantPassengerCount < 0) return false;
//       int totalPassengers = adultPassengerCount + childPassengerCount + infantPassengerCount;
//       if (totalPassengers <= 0) return false; // TC3: Min 1 total passenger
//       if (totalPassengers > 9) return false; // TC2: Max 9 total passengers (Max Boundary Fail is 10)

//       // --- 2. Passenger Ratio Validation (TC6, TC7) ---
//       // C5: Max 1 infant per adult (Infant Supervision)
//       if (infantPassengerCount > adultPassengerCount) return false; // TC7: Fail if 2 Infants : 1 Adult
      
//       // C4: Max 2 children per adult
//       if (adultPassengerCount > 0 && childPassengerCount > (adultPassengerCount * 2)) return false; // TC6: Fail if 3 Children : 1 Adult

//       // --- 3. Airport Code Validation (TC14, TC15) ---
//       String depCodeNormalized = departureAirportCode != null ? departureAirportCode.toUpperCase() : "";
//       String destCodeNormalized = destinationAirportCode != null ? destinationAirportCode.toUpperCase() : "";

//       // C11: Valid 3-letter IATA code (assuming list check covers length)
//       if (!VALID_AIRPORT_CODES.contains(depCodeNormalized) || !VALID_AIRPORT_CODES.contains(destCodeNormalized)) return false; // TC14
      
//       // C11: Departure and destination cannot be the same
//       if (depCodeNormalized.equals(destCodeNormalized)) return false; // TC15

//       // --- 4. Date Validation (TC8, TC9, TC10, TC11) ---
//       LocalDate depDateParsed;
//       LocalDate returnDateParsed = null;

//       try {
//          // C7: Strict date format and value check (e.g., 30/02/2026 is invalid)
//          depDateParsed = LocalDate.parse(departureDate, DATE_FORMATTER); // TC9, TC10
         
//          // C6: Departure Date must not be in the past (Current Date 20/10/2025)
//          if (depDateParsed.isBefore(CURRENT_DATE)) return false; // TC8

//          if (returnDate != null && !returnDate.trim().isEmpty()) {
//             returnDateParsed = LocalDate.parse(returnDate, DATE_FORMATTER);

//             // C8: Return Date cannot be before Departure Date
//             if (returnDateParsed.isBefore(depDateParsed)) return false; // TC11
//          }
//       } catch (DateTimeParseException e) {
//          // Catches C6 and C7 failures (wrong format or invalid value)
//          return false;
//       }

//       // --- 5. Seating Class and Emergency Row Restrictions (TC4, TC5, TC12, TC13) ---
      
//       // FIX: Normalize to handle case, remove the optional word " Class", and trim.
//       String normalizedClass = seatingClass.toLowerCase();
//       // Remove " class" if present at the end (case-insensitive due to toLowerCase()), and then trim any surrounding whitespace.
//       normalizedClass = normalizedClass.replaceAll(" class$", "").trim(); 

//       // C9: Seating class must be one of the four valid names (core term only)
//       if (!VALID_SEATING_CLASSES.contains(normalizedClass)) return false; // TC12
      
//       // C2: Restrict minors (children/infants) in First Class or Business Class.
//       // Now using strict equals as the string is normalized to the bare keyword.
//       if ((normalizedClass.equals("first") || normalizedClass.equals("business")) && 
//           (childPassengerCount > 0 || infantPassengerCount > 0)) { // TC5
//          return false;
//       }

//       // C3: Emergency Row requires all passengers to be adults (no children or infants).
//       if (emergencyRowSeating && (childPassengerCount > 0 || infantPassengerCount > 0)) { // TC4
//          return false;
//       }

//       // C10: Emergency Row is only allowed with Economy seating class.
//       // Now using strict equals as the string is normalized to the bare keyword.
//       if (emergencyRowSeating && !normalizedClass.equals("economy")) { // TC13
//          return false;
//       }
      
//       // --- 6. If all validations pass, initialize class attributes and return true ---
//       this.departureDate = departureDate;
//       this.departureAirportCode = departureAirportCode;
//       this.emergencyRowSeating = emergencyRowSeating;
//       this.returnDate = returnDate;
//       this.destinationAirportCode = destinationAirportCode;
//       this.seatingClass = seatingClass;
//       this.adultPassengerCount = adultPassengerCount;
//       this.childPassengerCount = childPassengerCount;
//       this.infantPassengerCount = infantPassengerCount;

//       return true;
//    }

//    // =======================================================================
//    // PUBLIC GETTER METHODS (ADDED TO SUPPORT TEST EXECUTION)
//    // =======================================================================

//    public String getDepartureDate() { return departureDate; }
//    public String getDepartureAirportCode() { return departureAirportCode; }
//    public boolean isEmergencyRowSeating() { return emergencyRowSeating; }
//    public String getReturnDate() { return returnDate; }
//    public String getDestinationAirportCode() { return destinationAirportCode; }
//    public String getSeatingClass() { return seatingClass; }
//    public int getAdultPassengerCount() { return adultPassengerCount; }
//    public int getChildPassengerCount() { return childPassengerCount; }
//    public int getInfantPassengerCount() { return infantPassengerCount; }
// }

package com.mycompany.flight;

import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern; // Added import for Pattern

public class FlightSearch {
   // Class attributes to be initialized on successful validation
   private String  departureDate;
   private String  departureAirportCode;
   private boolean emergencyRowSeating;
   private String  returnDate;
   private String  destinationAirportCode;
   private String  seatingClass;
   private int     adultPassengerCount;
   private int     childPassengerCount;
   private int     infantPassengerCount;

   // Constants for validation rules
   private static final int MAX_TOTAL_PASSENGERS = 9;
   private static final List<String> VALID_CLASSES = Arrays.asList("ECONOMY", "BUSINESS", "FIRST");
   
   // Date format: yyyy-MM-dd (ISO standard)
   private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                                               .withResolverStyle(ResolverStyle.STRICT);
                                                               
   // Pattern to enforce 3-letter IATA airport code format (case-insensitive)
   private static final Pattern CODE_PATTERN = Pattern.compile("^[a-zA-Z]{3}$");


   /**
    * Runs the flight search validation based on provided parameters.
    * If parameters are valid and meet all constraints, it initializes the class attributes and returns true.
    * Otherwise, it returns false.
    *
    * @param departureDate The date of departure (must be provided).
    * @param departureAirportCode The 3-letter code for the departure airport (must be provided).
    * @param emergencyRowSeating Flag indicating if emergency row seating is requested.
    * @param returnDate The date of return (can be null or empty for one-way).
    * @param destinationAirportCode The 3-letter code for the destination airport (must be provided).
    * @param seatingClass The requested seating class (Economy, Business, First - case insensitive).
    * @param adultPassengerCount Number of adult passengers (age 18+).
    * @param childPassengerCount Number of child passengers (age 2-17).
    * @param infantPassengerCount Number of infant passengers (age < 2).
    * @return true if the search parameters are valid and constraints are met, false otherwise.
    */
   public boolean runFlightSearch(String departureDate,    String departureAirportCode,   boolean emergencyRowSeating,
                                  String returnDate,       String destinationAirportCode, String seatingClass,
                                  int adultPassengerCount, int childPassengerCount,       int infantPassengerCount) {

      // --- 1. Basic Parameter Checks (Null/Empty/Format) ---

      // Critical strings must be non-null and non-empty
      if (departureDate == null || departureDate.trim().isEmpty() ||
          departureAirportCode == null || departureAirportCode.trim().isEmpty() ||
          destinationAirportCode == null || destinationAirportCode.trim().isEmpty() ||
          seatingClass == null || seatingClass.trim().isEmpty()) {
         System.err.println("Validation failed: Essential fields are missing.");
         return false;
      }
      
      // Check Airport Code Format (must be 3 letters)
      if (!CODE_PATTERN.matcher(departureAirportCode.trim()).matches() || 
          !CODE_PATTERN.matcher(destinationAirportCode.trim()).matches()) {
           System.err.println("Validation failed: Airport codes must be exactly 3 letters (IATA format).");
           return false;
      }


      // Check if Seating Class is valid (case-insensitive)
      String upperSeatingClass = seatingClass.trim().toUpperCase();
      if (!VALID_CLASSES.contains(upperSeatingClass)) {
         System.err.println("Validation failed: Invalid seating class: " + seatingClass);
         return false;
      }

      // Departure and Destination must be different
      if (departureAirportCode.trim().equalsIgnoreCase(destinationAirportCode.trim())) {
         System.err.println("Validation failed: Departure and destination airports cannot be the same.");
         return false;
      }

      // Passenger counts must be non-negative
      if (adultPassengerCount < 0 || childPassengerCount < 0 || infantPassengerCount < 0) {
         System.err.println("Validation failed: Passenger counts cannot be negative.");
         return false;
      }

      // --- 1.5. Date Validation (Addresses TC8, TC10, TC11) ---
      LocalDate depDate = null;
      LocalDate retDate = null;
      LocalDate today = LocalDate.now();

      try {
         // TC10: Invalid date value (strict parsing failure)
         depDate = LocalDate.parse(departureDate.trim(), DATE_FORMATTER);
      } catch (DateTimeParseException e) {
         System.err.println("Validation failed: Departure date format or value is invalid (e.g., must be yyyy-MM-dd).");
         return false;
      }

      // TC8: Departure date cannot be in the past. FIX: Use isBefore(today) instead of isBefore(today)
      // This allows 'today' as a valid departure date.
      if (depDate.isBefore(today)) {
         System.err.println("Validation failed: Departure date cannot be in the past.");
         return false;
      }

      if (returnDate != null && !returnDate.trim().isEmpty()) {
         try {
            retDate = LocalDate.parse(returnDate.trim(), DATE_FORMATTER);
         } catch (DateTimeParseException e) {
            System.err.println("Validation failed: Return date format or value is invalid (e.g., must be yyyy-MM-dd).");
            return false;
         }

         // TC11: Return date cannot be before departure date.
         if (retDate.isBefore(depDate)) {
            System.err.println("Validation failed: Return date cannot be before departure date.");
            return false;
         }
      }

      // --- 2. Passenger Totals and Ratios (Addresses TC1-1, TC1-2, TC6) ---

      int totalPassengers = adultPassengerCount + childPassengerCount + infantPassengerCount;

      // Rule: Must have at least one ADULT passenger (TC1-1 Basic valid search)
      if (adultPassengerCount < 1) {
         System.err.println("Validation failed: Must have at least one adult passenger.");
         return false;
      }
      
      // Check: Total passengers limit
      if (totalPassengers > MAX_TOTAL_PASSENGERS) {
         System.err.println("Validation failed: Total passengers exceed the maximum allowed (" + MAX_TOTAL_PASSENGERS + ").");
         return false;
      }

      // Infant to Adult Ratio (TC1-2): Infants must be accompanied by an adult.
      if (infantPassengerCount > adultPassengerCount) {
         System.err.println("Validation failed: Number of infants cannot exceed the number of adults.");
         return false;
      }
      
      // Child to Adult Ratio (TC6): Max 2 children per adult.
      if (childPassengerCount > 2 * adultPassengerCount) {
          System.err.println("Validation failed: Number of children cannot exceed twice the number of adults.");
          return false;
      }

      // --- 3. Seating Constraints (Addresses TC1-3 and TC1-4) ---

      // Minors are Child (2-17) and Infant (<2).
      boolean hasMinors = childPassengerCount > 0 || infantPassengerCount > 0;

      // Emergency Row Seating Restriction (TC1-4: no minors allowed)
      if (emergencyRowSeating && hasMinors) {
         System.err.println("Validation failed: Emergency row seating is not allowed with minors.");
         return false;
      }

      // Premium Seating Restriction (TC1-3: no minors allowed in First Class)
      if (upperSeatingClass.equals("FIRST") && hasMinors) {
         System.err.println("Validation failed: First Class seating is not allowed with minors.");
         return false;
      }


      // --- 4. Initialization (If all validations pass) ---
      
      this.departureDate = departureDate.trim();
      this.departureAirportCode = departureAirportCode.trim().toUpperCase();
      this.emergencyRowSeating = emergencyRowSeating;
      this.returnDate = returnDate != null ? returnDate.trim() : null;
      this.destinationAirportCode = destinationAirportCode.trim().toUpperCase();
      this.seatingClass = upperSeatingClass;
      this.adultPassengerCount = adultPassengerCount;
      this.childPassengerCount = childPassengerCount;
      this.infantPassengerCount = infantPassengerCount;

      // Validation passed
      return true;
   }

   // --- Getters for test verification (Addressing compilation errors) ---

   public String getDepartureDate() {
      return departureDate;
   }

   public String getDepartureAirportCode() {
      return departureAirportCode;
   }

   /**
    * Standard Java convention for boolean getters is 'is' followed by the property name.
    */
   public boolean isEmergencyRowSeating() {
      return emergencyRowSeating;
   }

   public String getReturnDate() {
      return returnDate;
   }

   public String getDestinationAirportCode() {
      return destinationAirportCode;
   }

   public String getSeatingClass() {
      return seatingClass;
   }

   public int getAdultPassengerCount() {
      return adultPassengerCount;
   }

   public int getChildPassengerCount() {
      return childPassengerCount;
   }

   public int getInfantPassengerCount() {
      return infantPassengerCount;
   }
}



FlightSearchTest.java




package com.mycompany.flight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Tests for the FlightSearch.runFlightSearch method (Activity 1.1).
 * Tests all 11 conditions, boundary values, and verifies attribute initialization (Note 7).
 * Assumed Current Date for tests: 20/10/2025.
 */
public class FlightSearchTest {

    private FlightSearch fs;

    // Standard valid parameters used as a base for tests
    private final String FUTURE_DATE_VALID = "25/10/2025";
    private final String FUTURE_DATE_RETURN = "30/10/2025";

    /**
     * Initializes a fresh FlightSearch object before each test.
     */
    @BeforeEach
    void setUp() {
        fs = new FlightSearch();
    }

    // --- Helper for Note 7 Checks ---

    /**
     * Asserts that attributes are NOT initialized, verifying logic for failed validation (Note 7).
     * We check that the default values (null, 0, false) are retained.
     */
    private void assertAttributesUninitialized() {
        assertNull(fs.getDepartureDate(), "Note 7: DepartureDate must be null after validation failure.");
        assertEquals(0, fs.getAdultPassengerCount(), "Note 7: AdultPassengerCount must be 0 after validation failure.");
        assertFalse(fs.isEmergencyRowSeating(), "Note 7: EmergencyRowSeating must be false after validation failure.");
    }

    /**
     * Asserts that attributes ARE initialized, verifying logic for successful validation (Note 7).
     * Compares stored attributes against the input parameters.
     */
    private void assertAttributesInitialized(String depDate, int adultCount, String seatingClass) {
        assertEquals(depDate, fs.getDepartureDate(), "Note 7: DepartureDate must be initialized on success.");
        assertEquals(adultCount, fs.getAdultPassengerCount(), "Note 7: AdultCount must be initialized on success.");
        assertEquals(seatingClass, fs.getSeatingClass(), "Note 7: SeatingClass must be initialized on success.");
    }

    // =======================================================================
    // TC1: VALID TEST CASE (Note 7 check with 4 combinations required)
    // =======================================================================

    /**
     * TC1-1: Valid test case (All inputs valid, basic combination).
     */
    @Test
    void testValidSearchCase1() {
        boolean result = fs.runFlightSearch(FUTURE_DATE_VALID, "syd", false,
                                            FUTURE_DATE_RETURN, "mel", "economy",
                                            2, 1, 0); // 2:1 ratio okay, no emergency, economy

        assertTrue(result, "TC1-1: Basic valid search should pass.");
        assertAttributesInitialized(FUTURE_DATE_VALID, 2, "economy");
    }

    /**
     * TC1-2: Valid test case (Boundary and infant combinations).
     */
    @Test
    void testValidSearchCase2() {
        boolean result = fs.runFlightSearch(FUTURE_DATE_VALID, "lax", false,
                                            "01/11/2025", "cdg", "premium economy",
                                            1, 2, 1); // Max C:A (2:1), Max I:A (1:1)

        assertTrue(result, "TC1-2: Boundary ratios (2:1, 1:1) should pass.");
        assertAttributesInitialized(FUTURE_DATE_VALID, 1, "premium economy");
    }

    /**
     * TC1-3: Valid test case (Testing First Class and Business Class without minors).
     */
    @Test
    void testValidSearchCase3() {
        boolean result = fs.runFlightSearch(FUTURE_DATE_VALID, "pvg", false,
                                            FUTURE_DATE_RETURN, "doh", "first",
                                            3, 0, 0); 

        assertTrue(result, "TC1-3: First Class without minors should pass.");
        assertAttributesInitialized(FUTURE_DATE_VALID, 3, "first");
    }
    
    /**
     * TC1-4: Valid test case (Testing Economy with Emergency Row).
     */
    @Test
    void testValidSearchCase4() {
        boolean result = fs.runFlightSearch(FUTURE_DATE_VALID, "mel", true,
                                            FUTURE_DATE_RETURN, "syd", "economy",
                                            2, 0, 0); 

        assertTrue(result, "TC1-4: Economy with Emergency Row (no minors) should pass.");
        assertAttributesInitialized(FUTURE_DATE_VALID, 2, "economy");
    }

    // =======================================================================
    // CONDITIONS 1, 4, 5 - PASSENGER COUNTS AND RATIOS
    // =======================================================================

    /**
     * TC2 (C1 Max): Total Passengers Max Boundary (10 total - Fail).
     */
    @Test
    void testInvalidTotalPassengerCountHighBoundary() {
        // 4 Adults, 6 Children = 10 total
        boolean result = fs.runFlightSearch(FUTURE_DATE_VALID, "syd", false,
                                            FUTURE_DATE_RETURN, "mel", "economy",
                                            4, 6, 0); 

        assertFalse(result, "TC2: C1 - Total passenger count (10) should fail validation.");
        assertAttributesUninitialized();
    }
    
    /**
     * TC3 (C1 Min): Total Passengers Min Boundary (0 total - Fail).
     */
    @Test
    void testInvalidTotalPassengerCountLowBoundary() {
        boolean result = fs.runFlightSearch(FUTURE_DATE_VALID, "syd", false,
                                            FUTURE_DATE_RETURN, "mel", "economy",
                                            0, 0, 0); 

        assertFalse(result, "TC3: C1 - Total passenger count (0) should fail validation.");
        assertAttributesUninitialized();
    }

    /**
     * TC6 (C4): Invalid Child Ratio (1 Adult, 3 Children - Fail).
     */
    @Test
    void testInvalidChildRatioExceeded() {
        boolean result = fs.runFlightSearch(FUTURE_DATE_VALID, "syd", false,
                                            FUTURE_DATE_RETURN, "mel", "economy",
                                            1, 3, 0); // Fails C4 (max 2)

        assertFalse(result, "TC6: C4 - Exceeds max 2 children per adult (3:1).");
        assertAttributesUninitialized();
    }

    /**
     * TC7 (C5): Invalid Infant Ratio (1 Adult, 2 Infants - Fail).
     */
    @Test
    void testInvalidInfantRatioExceeded() {
        boolean result = fs.runFlightSearch(FUTURE_DATE_VALID, "syd", false,
                                            FUTURE_DATE_RETURN, "mel", "economy",
                                            1, 0, 2); // Fails C5 (max 1)

        assertFalse(result, "TC7: C5 - Exceeds max 1 infant per adult (2:1).");
        assertAttributesUninitialized();
    }
    
    // =======================================================================
    // CONDITIONS 2, 3, 10 - SEATING RESTRICTIONS
    // =======================================================================

    /**
     * TC4 (C3): Infant in Emergency Row (Fail).
     */
    @Test
    void testInvalidInfantInEmergencyRow() {
        boolean result = fs.runFlightSearch(FUTURE_DATE_VALID, "syd", true,
                                            FUTURE_DATE_RETURN, "mel", "economy",
                                            2, 0, 1); // Fails C3

        assertFalse(result, "TC4: C3 - Infant passengers not allowed in Emergency Row.");
        assertAttributesUninitialized();
    }

    /**
     * TC5 (C2): Child in First Class (Fail).
     */
    @Test
    void testInvalidChildInFirstClass() {
        boolean result = fs.runFlightSearch(FUTURE_DATE_VALID, "syd", false,
                                            FUTURE_DATE_RETURN, "mel", "first",
                                            2, 1, 0); // Fails C2

        assertFalse(result, "TC5: C2 - Child passengers not allowed in First Class.");
        assertAttributesUninitialized();
    }
    
    /**
     * TC13 (C10): Emergency Row in Business Class (Fail).
     */
    @Test
    void testInvalidEmergencyRowNonEconomy() {
        boolean result = fs.runFlightSearch(FUTURE_DATE_VALID, "syd", true,
                                            FUTURE_DATE_RETURN, "mel", "business",
                                            1, 0, 0); // Fails C10

        assertFalse(result, "TC13: C10 - Emergency row only allowed with Economy.");
        assertAttributesUninitialized();
    }

    // =======================================================================
    // CONDITIONS 6, 7, 8 - DATE VALIDATION
    // =======================================================================

    /**
     * TC8 (C6): Departure Date in the Past (Fail).
     */
    @Test
    void testInvalidDepartureDateInPast() {
        String PAST_DATE = "19/10/2025"; // Past relative to 20/10/2025
        boolean result = fs.runFlightSearch(PAST_DATE, "syd", false,
                                            FUTURE_DATE_RETURN, "mel", "economy",
                                            1, 0, 0); 

        assertFalse(result, "TC8: C6 - Departure date cannot be in the past.");
        assertAttributesUninitialized();
    }

    /**
     * TC9 (C7): Invalid Date Format (Fail).
     */
    @Test
    void testInvalidDateFormat() {
        String WRONG_FORMAT = "2025-10-25"; // Must be DD/MM/YYYY
        boolean result = fs.runFlightSearch(WRONG_FORMAT, "syd", false,
                                            FUTURE_DATE_RETURN, "mel", "economy",
                                            1, 0, 0); 

        assertFalse(result, "TC9: C7 - Date format must be strictly DD/MM/YYYY.");
        assertAttributesUninitialized();
    }

    /**
     * TC10 (C7): Invalid Date Value (Strict Check) (Fail).
     */
    @Test
    void testInvalidDateStrictCheck() {
        String INVALID_DAY = "30/02/2026"; // Feb 2026 only has 28 days
        boolean result = fs.runFlightSearch(INVALID_DAY, "syd", false,
                                            FUTURE_DATE_RETURN, "mel", "economy",
                                            1, 0, 0); 

        assertFalse(result, "TC10: C7 - Invalid date value (strict parsing failure).");
        assertAttributesUninitialized();
    }

    /**
     * TC11 (C8): Return Date Before Departure (Fail).
     */
    @Test
    void testInvalidReturnDateBeforeDeparture() {
        boolean result = fs.runFlightSearch(FUTURE_DATE_RETURN, "syd", false,
                                            FUTURE_DATE_VALID, "mel", "economy", // Return 25th, Dep 30th -> Fail
                                            1, 0, 0); 

        assertFalse(result, "TC11: C8 - Return date cannot be before departure date.");
        assertAttributesUninitialized();
    }

    // =======================================================================
    // CONDITIONS 9, 11 - CLASS AND AIRPORTS
    // =======================================================================

    /**
     * TC12 (C9): Invalid Seating Class (Fail).
     */
    @Test
    void testInvalidSeatingClass() {
        String INVALID_CLASS = "comfort"; 
        boolean result = fs.runFlightSearch(FUTURE_DATE_VALID, "syd", false,
                                            FUTURE_DATE_RETURN, "mel", INVALID_CLASS,
                                            1, 0, 0); 

        assertFalse(result, "TC12: C9 - Seating class must be one of the four valid names.");
        assertAttributesUninitialized();
    }

    /**
     * TC14 (C11): Invalid Airport Codes (Fail).
     */
    @Test
    void testInvalidAirportCode() {
        String INVALID_CODE = "nyc"; 
        boolean result = fs.runFlightSearch(FUTURE_DATE_VALID, INVALID_CODE, false,
                                            FUTURE_DATE_RETURN, "mel", "economy",
                                            1, 0, 0); 

        assertFalse(result, "TC14: C11 - Airport codes must be one of the defined list.");
        assertAttributesUninitialized();
    }

    /**
     * TC15 (C11): Same Departure and Destination Airport (Fail).
     */
    @Test
    void testInvalidSameAirport() {
        boolean result = fs.runFlightSearch(FUTURE_DATE_VALID, "syd", false,
                                            FUTURE_DATE_RETURN, "syd", "economy",
                                            1, 0, 0); 

        assertFalse(result, "TC15: C11 - Departure and destination cannot be the same.");
        assertAttributesUninitialized();
    }
}




