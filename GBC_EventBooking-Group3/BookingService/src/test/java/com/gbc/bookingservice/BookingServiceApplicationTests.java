package com.gbc.bookingservice;

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
class BookingServiceApplicationTests {

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
	void createBookingTest() {
		String requestBody = """
                {
                    "userId": "user111",
                    "roomId": "room111",
                    "startTime": "2023-10-01T10:00:00",
                    "endTime": "2023-10-01T12:00:00",
                    "purpose": "Meeting"
                }
                """;

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/bookings")
				.then()
				.statusCode(201)
				.body("id", notNullValue())
				.body("userId", equalTo("user111"))
				.body("roomId", equalTo("room111"));
	}

	@Test
	void createBookingInvalidInputTest() {
		String invalidRequestBody = "{}";

		RestAssured.given()
				.contentType("application/json")
				.body(invalidRequestBody)
				.when()
				.post("/api/bookings")
				.then()
				.statusCode(400);
	}

	@Test
	void getAllBookingsTest() {
		RestAssured.given()
				.when()
				.get("/api/bookings")
				.then()
				.statusCode(200)
				.body("size()", greaterThan(0)); // Adjust based on existing data
	}

	@Test
	void getNonExistentBookingTest() {
		RestAssured.given()
				.when()
				.get("/api/bookings/{id}", "nonexistent-id")
				.then()
				.statusCode(404);
	}

	@Test
	void updateBookingTest() {
		String requestBody = """
                {
                    "purpose": "Updated Meeting"
                }
                """;

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.put("/api/bookings/{id}", "booking111") // Replace with actual user ID/check if new user created
				.then()
				.statusCode(200)
				.body("purpose", equalTo("Updated Meeting"));
	}

	@Test
	void updateNonExistentBookingTest() {
		String requestBody = """
                {
                    "purpose": "Updated Meeting"
                }
                """;

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.put("/api/bookings/{id}", "nonexistent-id")// Test with none existent ID
				.then()
				.statusCode(404);
	}

	@Test
	void deleteBookingTest() {
		RestAssured.given()
				.when()
				.delete("/api/bookings/{id}", "booking111") // Replace with actual user ID/check if new user created
				.then()
				.statusCode(204);
	}

	@Test
	void deleteNonExistentBookingTest() {
		RestAssured.given()
				.when()
				.delete("/api/bookings/{id}", "nonexistent-id") // Test with none existent ID
				.then()
				.statusCode(404);
	}
}
