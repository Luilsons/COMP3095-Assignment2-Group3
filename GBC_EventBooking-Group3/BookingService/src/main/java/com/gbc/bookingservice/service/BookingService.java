package com.gbc.bookingservice.service;

import com.gbc.bookingservice.model.Booking;
import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking createBooking(Booking booking);
    List<Booking> getAllBookings();
    Optional<Booking> getBookingById(String id);
    Booking updateBooking(String id, Booking updatedBooking);
    void deleteBooking(String id);
}
