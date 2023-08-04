package com.github.santosleijon.voidiummarket.common.eventstore;

import com.github.santosleijon.voidiummarket.common.eventstore.errors.DomainEventAlreadyPublished;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Component
public class EventStore {

    // TODO: How to ensure that events are ordered correctly by date?

    private final List<DomainEvent> events = new ArrayList<>();

    private final EventPublisher eventPublisher;

    @Autowired
    public EventStore(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publish(DomainEvent event) {
        var existingEventWithSameID = events.stream()
                .filter(e -> e.getId() == event.getId())
                .findFirst()
                .orElse(null);

        if (existingEventWithSameID != null) {
            throw new DomainEventAlreadyPublished(event);
        }

        events.add(event);

        eventPublisher.publish(event);
    }

    public List<DomainEvent> getEvents() {
        return Collections.unmodifiableList((events));
    }

    public List<DomainEvent> getEventsByAggregateIdAndName(UUID aggregateId, String aggregateName) {
        return events.stream()
                .filter(e -> e.getAggregateId().equals(aggregateId) && e.getAggregateName().equals(aggregateName))
                .collect(Collectors.toList());
    }

    public Map<UUID, List<DomainEvent>> getEventsByAggregateName(String aggregateName) {
        return events.stream()
                .filter(e -> e.getAggregateName().equals(aggregateName))
                .collect(groupingBy(DomainEvent::getAggregateId));
    }

    public void clear() {
        events.clear();
    }
}
