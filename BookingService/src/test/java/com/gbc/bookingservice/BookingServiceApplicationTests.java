package com.gbc.bookingservice;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class BookingServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

	@LocalServerPort
	private int port;

	@BeforeAll
	static void startContainer() {
		mongoDBContainer.start();
	}

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
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

		given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/bookings")
				.then()
				.statusCode(201)
				.body("id", notNullValue())
				.body("userId", equalTo("user111"))
				.body("roomId", equalTo("room111"))
				.body("purpose", equalTo("Meeting"));
	}

	@Test
	void createBookingInvalidInputTest() {
		String invalidRequestBody = "{}";

		given()
				.contentType("application/json")
				.body(invalidRequestBody)
				.when()
				.post("/api/bookings")
				.then()
				.statusCode(400);
	}

	@Test
	void getAllBookingsTest() {
		given()
				.when()
				.get("/api/bookings")
				.then()
				.statusCode(200)
				.body("size()", greaterThanOrEqualTo(0)); // Adjust based on existing data
	}

	@Test
	void getNonExistentBookingTest() {
		given()
				.when()
				.get("/api/bookings/{id}", "nonexistent-id")
				.then()
				.statusCode(404);
	}

	@Test
	void updateBookingTest() {
		String requestBody = """
                {
                    "userId": "user111",
                    "roomId": "room111",
                    "startTime": "2023-10-01T10:00:00",
                    "endTime": "2023-10-01T12:00:00",
                    "purpose": "Updated Meeting"
                }
                """;

		String bookingId = createTestBooking(); // Create a test booking to update

		given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.put("/api/bookings/{id}", bookingId)
				.then()
				.statusCode(200)
				.body("purpose", equalTo("Updated Meeting"));
	}

	@Test
	void deleteBookingTest() {
		String bookingId = createTestBooking(); // Create a test booking to delete

		given()
				.when()
				.delete("/api/bookings/{id}", bookingId)
				.then()
				.statusCode(204);
	}

	private String createTestBooking() {
		String requestBody = """
                {
                    "userId": "user111",
                    "roomId": "room111",
                    "startTime": "2023-10-01T10:00:00",
                    "endTime": "2023-10-01T12:00:00",
                    "purpose": "Test Booking"
                }
                """;

		return given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/bookings")
				.then()
				.statusCode(201)
				.extract()
				.path("id");
	}
}
