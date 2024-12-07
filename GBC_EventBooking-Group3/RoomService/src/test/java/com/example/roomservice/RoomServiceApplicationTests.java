package com.example.roomservice;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.TestcontainersConfiguration;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers // Enables Testcontainers support for tests
@Import(TestcontainersConfiguration.class)
class RoomServiceApplicationTests {

	@Container // Manages lifecycle of the PostgreSQL container automatically
	private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
			.withDatabaseName("testdb")
			.withUsername("testuser")
			.withPassword("testpassword");

	@LocalServerPort
	private Integer port;

	@BeforeAll
	static void init() {
		// Start the container before running the tests
		postgresContainer.start();
		System.setProperty("spring.datasource.url", postgresContainer.getJdbcUrl());
		System.setProperty("spring.datasource.username", postgresContainer.getUsername());
		System.setProperty("spring.datasource.password", postgresContainer.getPassword());
	}

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	void createRoomTest() {
		String requestBody = """
                {
                    "roomName": "Conference Room",
                    "capacity": 10,
                    "features": "Projector, Whiteboard",
                    "availability": true
                }
                """;

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/rooms")
				.then()
				.statusCode(201)
				.body("id", notNullValue())
				.body("roomName", equalTo("Conference Room"));
	}

	@Test
	void createRoomInvalidInputTest() {
		String invalidRequestBody = "{}";

		RestAssured.given()
				.contentType("application/json")
				.body(invalidRequestBody)
				.when()
				.post("/api/rooms")
				.then()
				.statusCode(400);
	}

	@Test
	void getAllRoomsTest() {
		RestAssured.given()
				.when()
				.get("/api/rooms")
				.then()
				.statusCode(200)
				.body("size()", greaterThan(0));
	}

	@Test
	void getRoomByIdTest() {
		RestAssured.given()
				.when()
				.get("/api/rooms/{id}", 1)
				.then()
				.statusCode(200)
				.body("roomName", equalTo("Conference Room"));
	}

	@Test
	void getNonExistentRoomTest() {
		RestAssured.given()
				.when()
				.get("/api/rooms/{id}", "invalid-id")
				.then()
				.statusCode(404);
	}

	@Test
	void updateRoomTest() {
		String requestBody = """
                {
                    "capacity": 20
                }
                """;

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.put("/api/rooms/{id}", "valid-id")
				.then()
				.statusCode(200)
				.body("capacity", equalTo(20));
	}

	@Test
	void updateNonExistentRoomTest() {
		String requestBody = """
                {
                    "capacity": 20
                }
                """;

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.put("/api/rooms/{id}", "nonexistent-id")
				.then()
				.statusCode(404);
	}

	@Test
	void deleteRoomTest() {
		RestAssured.given()
				.when()
				.delete("/api/rooms/{id}", "valid-id")
				.then()
				.statusCode(204);
	}

	@Test
	void deleteNonExistentRoomTest() {
		RestAssured.given()
				.when()
				.delete("/api/rooms/{id}", "nonexistent-id")
				.then()
				.statusCode(404);
	}

	@Test
	void updateRoomNonExistentIdTest() {
		String requestBody = """
                {
                    "capacity": 20
                }
                """;

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.put("/api/rooms/{id}", "nonexistent-id")
				.then()
				.statusCode(404)
				.body(equalTo("Room not found"));
	}
}
