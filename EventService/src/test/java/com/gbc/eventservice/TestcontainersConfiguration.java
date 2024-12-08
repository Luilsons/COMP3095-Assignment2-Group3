package com.gbc.eventservice;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestcontainersConfiguration {
	@Bean
	public MongoDBContainer mongoDbContainer() {
		MongoDBContainer container = new MongoDBContainer(DockerImageName.parse("mongo:6.0"));
		container.start();
		System.setProperty("spring.data.mongodb.uri", container.getReplicaSetUrl());
		return container;
	}
}

