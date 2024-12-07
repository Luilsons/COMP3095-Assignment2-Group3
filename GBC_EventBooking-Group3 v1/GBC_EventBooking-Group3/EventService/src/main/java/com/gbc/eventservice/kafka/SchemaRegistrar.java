package com.gbc.eventservice.kafka;

import org.apache.avro.Schema;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class SchemaRegistrar implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        Schema schema = new Schema.Parser().parse(new File("src/main/avro/BookingEvent.avsc"));
        SchemaRegistryClientUtil.registerSchema("booking-events-value", schema);
        System.out.println("Schema registered successfully!");
    }
}
