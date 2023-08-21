package com.github.santosleijon.voidiummarket.common.eventstreaming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.stereotype.Component;

@Component
public abstract class EventListener<T extends DomainEvent> {

    protected Class<T> eventType;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public EventListener(Class<T> eventType) {
        this.eventType = eventType;
    }

    @KafkaHandler
    private void handleMessage(String message) {
        try {
            T event = objectMapper.readValue(message, eventType);
            handle(event);
        } catch (JsonProcessingException e) {
            // Ignore messages that can't be deserialized to expected event
        }
    }

    public abstract void handle(T event);
}
