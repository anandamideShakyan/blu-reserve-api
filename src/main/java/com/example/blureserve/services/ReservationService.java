package com.example.blureserve.services;

import com.example.blureserve.models.Booking;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class ReservationService {

    private final Map<String, Map<String, List<String>>> locations = new HashMap<>();
    private final List<Booking> bookings = new ArrayList<>();

    public ReservationService() {
        // Initializing locations with example data
        initializeLocations();
    }

    private void initializeLocations() {
        // Initializing available slots for each location with empty booked seat lists
        for (String location : Arrays.asList("riviera", "pyramid")) {
            Map<String, List<String>> timeSlots = new LinkedHashMap<>();
            for (int hour = 0; hour < 24; hour++) {
                String firstSlot = String.format("%02d:00-%02d:30", hour, hour);
                String secondSlot = String.format("%02d:30-%02d:00", hour, (hour + 1) % 24);
                timeSlots.put(firstSlot, new ArrayList<>());
                timeSlots.put(secondSlot, new ArrayList<>());
            }
            locations.put(location, timeSlots);
        }
    }

    public List<Map<String, Object>> getUserBookings(String userID) {
        List<Map<String, Object>> userBookings = new ArrayList<>();
        String currentDate = LocalDate.now().toString();
        LocalTime now = LocalTime.now();

        for (Booking booking : bookings) {
            if (booking.getUserId().equals(userID)) {
                Map<String, Object> bookingDetails = new HashMap<>();
                bookingDetails.put("user_id", booking.getUserId());
                bookingDetails.put("location", booking.getLocation());
                bookingDetails.put("slot", booking.getSlot());
                bookingDetails.put("date", booking.getDate());
                bookingDetails.put("seats", booking.getSeats());

                // Check if the slot is upcoming or has passed
                String[] timeParts = booking.getSlot().split("-");
                LocalTime slotTime = LocalTime.parse(timeParts[0]);
                bookingDetails.put("upcoming", booking.getDate().equals(currentDate) && slotTime.isAfter(now));

                userBookings.add(bookingDetails);
            }
        }
        return userBookings;
    }

    public ResponseEntity<Object> bookSeats(String location, String slot, int numberOfSeats, String userID) {
        LocalTime now = LocalTime.now();
        String currentDate = LocalDate.now().toString();

        // Check if the location and slot exist
        if (!locations.containsKey(location) || !locations.get(location).containsKey(slot)) {
            return ResponseEntity.badRequest().body("Invalid location or slot.");
        }

        String[] timeParts = slot.split("-");
        LocalTime slotStartTime = LocalTime.parse(timeParts[0]);
        if (slotStartTime.isBefore(now)) {
            return ResponseEntity.badRequest().body("Cannot book seats for a past time slot.");
        }

        List<String> bookedSeats = locations.get(location).get(slot);
        int availableSeats = 50 - bookedSeats.size();

        if (numberOfSeats > availableSeats) {
            return ResponseEntity.ok("Only " + availableSeats
                    + " seats are available in this slot. Please reduce the number of seats or choose another slot.");
        }

        // Allocate seats and update the bookings
        List<String> allocatedSeats = new ArrayList<>();
        for (int i = 1; i <= 50 && allocatedSeats.size() < numberOfSeats; i++) {
            String seat = String.valueOf(i);
            if (!bookedSeats.contains(seat)) {
                allocatedSeats.add(seat);
                bookedSeats.add(seat);
            }
        }

        // Create and store the booking
        Booking newBooking = new Booking(userID, location, slot, currentDate, allocatedSeats);
        bookings.add(newBooking);

        return ResponseEntity.ok("Booking successful. Seats allocated: " + allocatedSeats);
    }
}
