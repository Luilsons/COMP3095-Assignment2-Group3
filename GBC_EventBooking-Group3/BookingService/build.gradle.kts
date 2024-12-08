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
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("io.confluent:kafka-avro-serializer:7.3.0")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.mockito")
	}
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
	testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
	testImplementation("io.rest-assured:rest-assured:5.3.0")
	testImplementation("org.hamcrest:hamcrest:2.2")
	testImplementation("org.testcontainers:testcontainers:1.18.3")
	testImplementation("org.testcontainers:mongodb:1.18.3")
	testImplementation("org.testcontainers:junit-jupiter:1.18.3")
	testRuntimeOnly("org.slf4j:slf4j-simple:2.0.7")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

springBoot {
	mainClass.set("com.gbc.bookingservice.BookingServiceApplication")
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
}