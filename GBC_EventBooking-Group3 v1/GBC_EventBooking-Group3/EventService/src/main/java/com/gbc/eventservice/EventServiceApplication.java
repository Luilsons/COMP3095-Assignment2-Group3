package com.gbc.eventservice;

import com.gbc.eventservice.kafka.SchemaRegistrar;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EventServiceApplication implements CommandLineRunner {

	private final SchemaRegistrar schemaRegistrar;

	public EventServiceApplication(SchemaRegistrar schemaRegistrar) {
		this.schemaRegistrar = schemaRegistrar;
	}

	public static void main(String[] args) {
		SpringApplication.run(EventServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		schemaRegistrar.registerSchema();
	}
}
