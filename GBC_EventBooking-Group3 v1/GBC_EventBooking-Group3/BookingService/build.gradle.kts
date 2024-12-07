plugins {
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
	id("com.github.davidmc24.gradle.plugin.avro") version "1.6.0"
	kotlin("jvm") version "1.8.10"
}

group = "com.gbc"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(22))
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://packages.confluent.io/maven/") }
}



dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.apache.avro:avro:1.11.1") // Avro dependency
	implementation("org.springframework.kafka:spring-kafka")
	implementation("io.confluent:kafka-avro-serializer:7.3.0")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mongodb")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("io.rest-assured:rest-assured:5.3.0")
	implementation("io.confluent:kafka-avro-serializer:7.4.0")
	implementation("org.apache.avro:avro:1.11.2")
	implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j:3.1.2")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

}

// Configure Avro Plugin for schema generation
tasks.named<com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask>("generateAvroJava") {
	source("src/main/resources/avro") // Path to Avro schema files
	setOutputDir(file("build/generated-main-avro-java")) // Specify output directory
}

sourceSets {
	main {
		java {
			srcDir("build/generated-main-avro-java") // Add generated Avro classes to the source set
		}
	}
}

springBoot {
	mainClass.set("com.gbc.bookingservice.BookingServiceApplication")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
