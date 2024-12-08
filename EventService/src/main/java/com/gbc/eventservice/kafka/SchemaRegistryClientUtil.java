package com.gbc.eventservice.kafka;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import org.apache.avro.Schema;

import java.io.IOException;

public class SchemaRegistryClientUtil {

    private static final String SCHEMA_REGISTRY_URL = "http://schema-registry:8081";

    public static void registerSchema(String subject, Schema schema) throws IOException, RestClientException {
        SchemaRegistryClient client = new CachedSchemaRegistryClient(SCHEMA_REGISTRY_URL, 10);
        client.register(subject, schema);
    }
}
