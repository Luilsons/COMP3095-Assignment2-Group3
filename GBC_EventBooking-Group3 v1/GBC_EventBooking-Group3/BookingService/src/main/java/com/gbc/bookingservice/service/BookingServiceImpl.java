package com.gbc.bookingservice.service;

import com.gbc.bookingservice.kafka.BookingEventProducer;
import com.gbc.bookingservice.model.Booking;
import com.gbc.bookingservice.repository.BookingRepository;
import com.gbc.bookingservice.avro.BookingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private static final String ROOM_SERVICE_URL = "http://ROOM_SERVICE_URL/api/rooms/";

    private final BookingRepository bookingRepository;
    private final RestTemplate restTemplate;
    private final BookingEventProducer bookingEventProducer;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, RestTemplate restTemplate, BookingEventProducer bookingEventProducer) {
        this.bookingRepository = bookingRepository;
        this.restTemplate = restTemplate;
        this.bookingEventProducer = bookingEventProducer;
    }

    @Override
    public Booking createBooking(Booking booking) {
        String roomServiceUrl = ROOM_SERVICE_URL + booking.getRoomId()
                + "/availability?startTime=" + booking.getStartTime()
                + "&endTime=" + booking.getEndTime();

        Boolean isAvailable = restTemplate.getForObject(roomServiceUrl, Boolean.class);

        if (Boolean.FALSE.equals(isAvailable)) {
            throw new RuntimeException("Room is not available for the requested time period.");
        }

        List<Booking> overlappingBookings = bookingRepository.findByRoomIdAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                booking.getRoomId(), booking.getEndTime(), booking.getStartTime());

        if (!overlappingBookings.isEmpty()) {
            throw new RuntimeException("Room is already booked for the requested time period.");
        }

        Booking savedBooking = bookingRepository.save(booking);

        // Publish event to Kafka
        BookingEvent event = BookingEvent.newBuilder()
                .setId(savedBooking.getId())
                .setUserId(savedBooking.getUserId())
                .setRoomId(savedBooking.getRoomId())
                .setStartTime(savedBooking.getStartTime().toString())
                .setEndTime(savedBooking.getEndTime().toString())
                .setPurpose(savedBooking.getPurpose())
                .build();
        bookingEventProducer.sendEvent("booking-events", event);

        return savedBooking;
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Optional<Booking> getBookingById(String id) {
        return bookingRepository.findById(id);
    }

    @Override
    public Booking updateBooking(String id, Booking updatedBooking) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        existingBooking.setUserId(updatedBooking.getUserId());
        existingBooking.setRoomId(updatedBooking.getRoomId());
        existingBooking.setStartTime(updatedBooking.getStartTime());
        existingBooking.setEndTime(updatedBooking.getEndTime());
        existingBooking.setPurpose(updatedBooking.getPurpose());

        return bookingRepository.save(existingBooking);
    }

    @Override
    public void deleteBooking(String id) {
        if (!bookingRepository.existsById(id)) {
            throw new RuntimeException("Booking not found");
        }
        bookingRepository.deleteById(id);
    }
}
