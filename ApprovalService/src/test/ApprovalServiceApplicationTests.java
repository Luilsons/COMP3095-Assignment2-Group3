package com.example.approvalservice;

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
class ApprovalServiceApplicationTests {

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
    void createApprovalTest() {
        String requestBody = """
                {
                    "eventId": "event111",
                    "approverId": "user111",
                    "status": "pending",
                    "comments": "Approval pending"
                }
                """;

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/approvals")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("status", equalTo("pending"));
    }

    @Test
    void createApprovalInvalidInputTest() {
        String invalidRequestBody = "{}";

        RestAssured.given()
                .contentType("application/json")
                .body(invalidRequestBody)
                .when()
                .post("/api/approvals")
                .then()
                .statusCode(400);
    }

    @Test
    void getAllApprovalsTest() {
        RestAssured.given()
                .when()
                .get("/api/approvals")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    void updateApprovalTest() {
        String requestBody = """
                {
                    "status": "approved",
                    "comments": "Approved"
                }
                """;

        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .put("/api/approvals/{id}", "event111")// Replace with actual user ID/check if new user created
                .then()
                .statusCode(200)
                .body("status", equalTo("approved"));
    }

    @Test
    void updateApprovalUnauthorizedTest() {
        String requestBody = """
                {
                    "status": "approved",
                    "comments": "Approved"
                }
                """;

        // Test without User-Role header
        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .put("/api/approvals/{id}", "event111")// Replace with actual user ID/check if new user created
                .then()
                .statusCode(403);

        // Test with invalid User-Role header
        RestAssured.given()
                .contentType("application/json")
                .header("User-Role", "guest")
                .body(requestBody)
                .when()
                .put("/api/approvals/{id}", "event111")// Replace with actual user ID/check if new user created
                .then()
                .statusCode(403);
    }

    @Test
    void getNonExistentApprovalTest() {
        RestAssured.given()
                .when()
                .get("/api/approvals/{id}", "nonexistent-id")// Test with none existent ID
                .then()
                .statusCode(404);
    }

    @Test
    void deleteApprovalTest() {
        RestAssured.given()
                .when()
                .delete("/api/approvals/{id}", "event111")// Replace with actual user ID/check if new user created
                .then()
                .statusCode(204);
    }

    @Test
    void deleteNonExistentApprovalTest() {
        RestAssured.given()
                .when()
                .delete("/api/approvals/{id}", "nonexistent-id") // Test with none existent ID
                .then()
                .statusCode(404);
    }
}
