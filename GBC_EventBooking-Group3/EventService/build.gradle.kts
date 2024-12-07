plugins {
	java
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("jvm") version "1.8.10"
}

group = "com.gbc"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(22)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://packages.confluent.io/maven/") }
}

dependencies {
	// Spring Boot main dependencies
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("io.confluent:kafka-avro-serializer:7.4.0")
	implementation("org.apache.avro:avro:1.11.2")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// Spring Boot Test dependencies
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.mockito") // Exclude Mockito if unused
	}
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
	testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

	// Rest-Assured for API testing
	testImplementation("io.rest-assured:rest-assured:5.3.0")

	// Hamcrest for matchers
	testImplementation("org.hamcrest:hamcrest:2.2")

	// Testcontainers for MongoDB and JUnit integration
	testImplementation("org.testcontainers:testcontainers:1.18.3")
	testImplementation("org.testcontainers:mongodb:1.18.3")
	testImplementation("org.testcontainers:junit-jupiter:1.18.3")

	// Logging during tests
	testRuntimeOnly("org.slf4j:slf4j-simple:2.0.7")

	implementation("io.confluent:kafka-avro-serializer:7.3.0")
	implementation("org.apache.avro:avro:1.11.1")

}

tasks.withType<Test> {
	useJUnitPlatform()
	testLogging {
		events("PASSED", "FAILED", "SKIPPED")
	}
}

springBoot {
	mainClass.set("com.gbc.eventservice.EventServiceApplication")
}

avro {
	setSource(file("src/main/resources/avro")) // Directory containing Avro schema files
	setOutputDir(file("build/generated-main-avro-java")) // Output directory for generated classes
	stringType.set("String") // Use Java String for Avro strings
}

sourceSets {
	main {
		java {
			srcDirs("build/generated-main-avro-java") // Include generated Avro classes in the main source set
		}
	}
}9