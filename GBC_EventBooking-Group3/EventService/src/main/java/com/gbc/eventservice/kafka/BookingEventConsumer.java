package com.gbc.eventservice.kafka;

import com.gbc.eventservice.avro.BookingEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BookingEventConsumer {

    @KafkaListener(topics = "booking-events", groupId = "event-service-group")
    public void consumeBookingEvent(ConsumerRecord<String, BookingEvent> record) {
        BookingEvent event = record.value();

        // Log the event for debugging
        System.out.println("Received booking event: " + event);

        // Process the booking event
        processBookingEvent(event);
    }

    private void processBookingEvent(BookingEvent event) {
        if (event.getBookingId() == null || event.getUserId() == null) {
            System.err.println("Invalid event: Missing required fields");
            return;
        }

        // Process valid events
        System.out.println("Processing booking event: " + event);
    }

}
