package com.example.userservice;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class UserServiceApplicationTests {

    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        postgresContainer.start();
    }

    @Test
    void createUserTest() {
        String requestBody = """
                {
                    "name": "John Doe",
                    "email": "johndoe@example.com",
                    "role": "student",
                    "userType": "student"
                }
                """;

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("John Doe"))
                .body("email", equalTo("johndoe@example.com"));
    }

    @Test
    void createUserInvalidInputTest() {
        String invalidRequestBody = """
                {
                    "name": "", // Missing email
                    "role": "student",
                    "userType": "student"
                }
                """;

        // Test creating a user with invalid input
        RestAssured.given()
                .contentType("application/json")
                .body(invalidRequestBody)
                .when()
                .post("/api/users")
                .then()
                .statusCode(400); // Expect 400 Bad Request
    }

    @Test
    void getAllUsersTest() {
        RestAssured.given()
                .when()
                .get("/api/users")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    void updateUserTest() {
        String requestBody = """
                {
                    "name": "John Doe Updated"
                }
                """;

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .put("/api/users/{id}", "user111") // Replace with actual user ID/check if new user created
                .then()
                .statusCode(200)
                .body("name", equalTo("John Doe Updated"));
    }

    @Test
    void updateUserNotFoundTest() {
        String requestBody = """
                {
                    "name": "Non-existent User"
                }
                """;

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .put("/api/users/{id}", "nonexistent-id") // Test with a non-existent user ID
                .then()
                .statusCode(404); // Expect 404 Not Found
    }

    @Test
    void deleteUserTest() {
        RestAssured.given()
                .when()
                .delete("/api/users/{id}", "user111") // Replace with actual user ID/check if new user created
                .then()
                .statusCode(204);
    }

    @Test
    void deleteUserNotFoundTest() {
        RestAssured.given()
                .when()
                .delete("/api/users/{id}", "nonexistent-id") // Test with a non-existent user ID
                .then()
                .statusCode(404); // Expect 404 Not Found
    }
}
