package com.gbc.eventservice;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class EventServiceApplicationTests {

	static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
		mongoDBContainer.start();
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
				.statusCode(201)
				.body("id", notNullValue())
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
				.statusCode(200)
				.body("size()", greaterThan(0));
	}

	@Test
	void updateEventTest() {
		String requestBody = """
                {
                    "eventName": "Updated Tech Conference"
                }
                """;

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.put("/api/events/{id}", "event111") // Replace with actual user ID/check if new user created
				.then()
				.statusCode(200)
				.body("eventName", equalTo("Updated Tech Conference"));
	}

	@Test
	void deleteEventTest() {
		RestAssured.given()
				.when()
				.delete("/api/events/{id}", "event111") // Replace with actual user ID/check if new user created
				.then()
				.statusCode(204);
	}

	@Test
	void getNonExistentEventTest() {
		RestAssured.given()
				.when()
				.get("/api/events/{id}", "nonexistent-id") // Test with a non-existent event ID
				.then()
				.statusCode(404);
	}

	@Test
	void updateNonExistentEventTest() {
		String requestBody = """
                {
                    "eventName": "Non-existent Event"
                }
                """;

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.put("/api/events/{id}", "nonexistent-id") // Test with a non-existent event ID
				.then()
				.statusCode(404);
	}

	@Test
	void deleteNonExistentEventTest() {
		RestAssured.given()
				.when()
				.delete("/api/events/{id}", "nonexistent-id") // Test with a non-existent event ID
				.then()
				.statusCode(404);
	}
}
