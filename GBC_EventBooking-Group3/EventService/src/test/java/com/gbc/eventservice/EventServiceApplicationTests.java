package com.gbc.eventservice;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.web.server.LocalServerPort;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
public class EventServiceApplicationTests {

	@LocalServerPort
	private int port;

	@BeforeEach
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	void createEventTest() {
		String requestBody = """
                {
                    "eventName": "Tech Conference",
                    "organizerId": "organizer123",
                    "eventType": "conference",
                    "expectedAttendees": 100
                }
                """;

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/events")
				.then()
				.statusCode(HttpStatus.CREATED.value())
				.body("eventName", equalTo("Tech Conference"))
				.body("eventType", equalTo("conference"))
				.body("expectedAttendees", equalTo(100));
	}

	@Test
	void getAllEventsTest() {
		RestAssured.given()
				.when()
				.get("/api/events")
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("size()", greaterThanOrEqualTo(0));
	}

	@Test
	void getNonExistentEventTest() {
		RestAssured.given()
				.when()
				.get("/api/events/{id}", "nonexistent-id")
				.then()
				.statusCode(HttpStatus.NOT_FOUND.value());
	}
}
