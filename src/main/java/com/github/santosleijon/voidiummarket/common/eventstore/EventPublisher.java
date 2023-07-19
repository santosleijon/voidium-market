package com.github.santosleijon.voidiummarket.common.eventstore;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventPublisher {

    private final Map<String, List<EventSubscriber>> subscribers = new HashMap<>();

    public void registerPublisher(String eventType, EventSubscriber subscriber) {
        if (!subscribers.containsKey(eventType)) {
            subscribers.put(eventType, new ArrayList<>());
        }

        subscribers.get(eventType).add(subscriber);
    }

    public void deregisterPublisher(String eventType, EventSubscriber subscriber) {
        if (subscribers.containsKey(eventType)) {
            subscribers.get(eventType).remove(subscriber);
        }
    }

    public void publish(DomainEvent event) {
        if (subscribers.containsKey(event.getType())) {
            subscribers.get(event.getType()).forEach(subscriber -> subscriber.receive(event));
        }
    }
}
