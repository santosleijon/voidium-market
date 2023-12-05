package com.github.santosleijon.voidiummarket.common.eventstreaming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

public class DomainEventSerializer implements Serializer<DomainEvent>, Deserializer<DomainEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public byte[] serialize(String topic, DomainEvent domainEvent) {
        if (domainEvent == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsBytes(domainEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing DomainEvent", e);
        }
    }

    @Override
    public DomainEvent deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }

        try {
            return objectMapper.readValue(data, DomainEvent.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing DomainEvent", e);
        }
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public void close() {
    }
}