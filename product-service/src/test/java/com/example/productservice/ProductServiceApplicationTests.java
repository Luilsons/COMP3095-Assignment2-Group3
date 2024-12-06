package com.example.productservice;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers // Annotation to enable TestContainers support
class ProductServiceApplicationTests {

	@Container // Ensures the container lifecycle is managed automatically
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
			.withExposedPorts(27017); // Expose default MongoDB port

	@LocalServerPort
	private Integer port;

	static {
		// Start the container and set the MongoDB URI
		mongoDBContainer.start();
		System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl("testdb")); // Use a test database
	}

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	void createProductTest() {
		String requestBody = """
                {
                    "name": "Playstation",
                    "description": "Playstation 5",
                    "price": 500
                }
                """;

		// Test creating a new product
		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/product")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("Playstation"))
				.body("description", Matchers.equalTo("Playstation 5"))
				.body("price", Matchers.equalTo(500));
	}

	@Test
	void createProductInvalidInputTest() {
		String invalidRequestBody = """
                {
                    "name": "",
                    "description": "Playstation 5",
                    "price": -100
                }
                """;

		// Test creating a product with invalid input
		RestAssured.given()
				.contentType("application/json")
				.body(invalidRequestBody)
				.when()
				.post("/api/product")
				.then()
				.statusCode(400);
	}

	@Test
	void getAllProductsTest() {
		// Ensure there is at least one product in the database
		createProductTest();

		// Test retrieving all products
		RestAssured.given()
				.when()
				.get("/api/product")
				.then()
				.statusCode(200)
				.body("size()", Matchers.greaterThan(0)); // Expect at least one product in the list
	}

	@Test
	void updateProductTest() {
		// Ensure a product exists before updating
		String createdProductId = createProductAndReturnId();

		String requestBody = """
                {
                    "name": "Playstation Updated",
                    "description": "Updated description",
                    "price": 550
                }
                """;

		// Test updating an existing product by its ID
		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.put("/api/product/{productId}", createdProductId)
				.then()
				.statusCode(200)
				.body("name", Matchers.equalTo("Playstation Updated"));
	}

	@Test
	void deleteProductTest() {
		// Ensure a product exists before deleting
		String createdProductId = createProductAndReturnId();

		// Test deleting an existing product by its ID
		RestAssured.given()
				.when()
				.delete("/api/product/{productId}", createdProductId)
				.then()
				.statusCode(204);
	}

	@Test
	void getNonExistentProductTest() {
		// Test retrieving a product that does not exist
		RestAssured.given()
				.when()
				.get("/api/product/{productId}", "nonexistent-id")
				.then()
				.statusCode(404);
	}

	// Helper method to create a product and return its ID
	private String createProductAndReturnId() {
		String requestBody = """
                {
                    "name": "Temporary Product",
                    "description": "Temporary Description",
                    "price": 100
                }
                """;

		return RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/product")
				.then()
				.statusCode(201)
				.extract()
				.path("id");
	}
}
