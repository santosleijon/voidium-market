package com.github.santosleijon.voidiummarket.common.eventstreaming;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class DomainEventSerializer implements Deserializer<DomainEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

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
}