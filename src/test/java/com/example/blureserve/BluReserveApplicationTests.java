package com.example.seatreservation.service;

import com.example.blureserve.models.Booking;
import com.example.blureserve.services.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private List<Booking> bookings;

    @Mock
    private Map<String, Map<String, List<String>>> locations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookings = new ArrayList<>();
        locations = new HashMap<>();

        // Sample test data setup
        Map<String, List<String>> cafeSlots = new HashMap<>();
        cafeSlots.put("9:00-9:30", new ArrayList<>(List.of("1", "2")));
        locations.put("riviera", cafeSlots);

        reservationService = new ReservationService(bookings, locations);
    }

    @Test
    void testGetBookingsForUser() {
        // Setup: Add a sample booking
        Booking sampleBooking = new Booking("3edww34t", "riviera", "9:00-9:30", "18/11/2024", List.of("1", "2"));
        bookings.add(sampleBooking);

        // Act: Call getBookingsForUser
        List<Booking> result = reservationService.getBookingsForUser("3edww34t");

        // Assert: Check if the result matches the expected value
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(sampleBooking, result.get(0));
    }

    @Test
    void testReserveSeats_Success() {
        // Arrange: Create a reservation request
        ReservationRequest request = new ReservationRequest("riviera", "9:00-9:30", 1);

        // Act: Call reserveSeats
        boolean isSuccess = reservationService.reserveSeats(request, "3edww34t", "18/11/2024");

        // Assert: Check if the reservation was successful
        assertTrue(isSuccess);
        assertEquals(1, bookings.size());
        assertEquals(3, locations.get("riviera").get("9:00-9:30").size());  // Seats "1", "2" and new seat added
    }

    @Test
    void testReserveSeats_FailureDueToFullSlot() {
        // Arrange: Create a reservation request with more seats than available
        ReservationRequest request = new ReservationRequest("riviera", "9:00-9:30", 3);

        // Act: Call reserveSeats
        boolean isSuccess = reservationService.reserveSeats(request, "3edww34t", "18/11/2024");

        // Assert: Check if the reservation failed
        assertFalse(isSuccess);
    }

    @Test
    void testReserveSeats_FailureForPastTimeSlot() {
        // Arrange: Create a reservation request for a past time slot
        ReservationRequest request = new ReservationRequest("riviera", "8:00-8:30", 2);

        // Act: Call reserveSeats
        boolean isSuccess = reservationService.reserveSeats(request, "3edww34t", "18/11/2024");

        // Assert: Check if the reservation failed due to past time
        assertFalse(isSuccess, "Reservation should not be allowed for past time slots.");
    }
}
