package com.github.santosleijon.voidiummarket.common.eventstore;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Component
public class EventStore {

    // TODO: How to ensure that events are ordered correctly by date?

    private final List<DomainEvent> events = new ArrayList<>();

    public void publish(DomainEvent event) throws DomainEventAlreadyPublished {
        var existingEventWithSameID = events.stream()
                .filter(e -> e.getId() == event.getId())
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

    public List<DomainEvent> getEventsByAggregateIdAndName(UUID aggregateId, String aggregateName) {
        return events.stream()
                .filter(e -> e.getAggregateId() == aggregateId && e.getAggregateName().equals(aggregateName))
                .collect(Collectors.toList());
    }

    public Map<UUID, List<DomainEvent>> getEventsByAggregateName(String aggregateName) {
        return events.stream()
                .filter(e -> e.getAggregateName().equals(aggregateName))
                .collect(groupingBy(DomainEvent::getAggregateId));
    }
}
