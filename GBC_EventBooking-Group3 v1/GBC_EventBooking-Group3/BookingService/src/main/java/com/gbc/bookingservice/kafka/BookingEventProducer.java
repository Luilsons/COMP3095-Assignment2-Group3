package com.gbc.bookingservice.kafka;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class BookingEventProducer {

    private final KafkaTemplate<String, SpecificRecord> kafkaTemplate;

    @Autowired
    public BookingEventProducer(KafkaTemplate<String, SpecificRecord> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(String topic, SpecificRecord event) {
        kafkaTemplate.send(topic, event);
    }
}
