package com.github.santosleijon.voidiummarket.common.eventstore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EventStore {

    // TODO: How to ensure that events are ordered correctly by date?

    private final List<DomainEvent> events = new ArrayList<>();

    public void publish(DomainEvent event) throws DomainEventAlreadyPublished {
        var existingEventWithSameID = events.stream()
                .filter(e -> e.id() == event.id())
                .findFirst()
                .orElse(null);

        if (existingEventWithSameID != null) {
            throw new DomainEventAlreadyPublished(event);
        }

        events.add(event);
    }

    public List<DomainEvent> getEvents() {
        return Collections.unmodifiableList((events));
    }

    public List<DomainEvent> getEventsForAggregate(UUID aggregateId) {
        return events.stream()
                .filter(e -> e.aggregateId() == aggregateId)
                .collect(Collectors.toList());
    }
}
