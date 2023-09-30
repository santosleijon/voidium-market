package com.github.santosleijon.voidiummarket.common.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.santosleijon.voidiummarket.common.eventstore.errors.AggregateVersionIsOutOfDate;
import com.github.santosleijon.voidiummarket.common.eventstore.errors.DomainEventAlreadyPublished;
import com.github.santosleijon.voidiummarket.common.eventstore.errors.DomainEventFailedToPublish;
import com.github.santosleijon.voidiummarket.common.eventstreaming.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Component
public class EventStore {

    // TODO: Replace in-memory list with PostgreSQL table
    private final List<DomainEvent> events = Collections.synchronizedList(new ArrayList<>());

    private final EventStoreDAO eventStoreDAO;

    private final EventPublisher eventPublisher;

    @Autowired
    public EventStore(EventStoreDAO eventStoreDAO, EventPublisher eventPublisher) {
        this.eventStoreDAO = eventStoreDAO;
        this.eventPublisher = eventPublisher;
    }

    public void publish(DomainEvent event, int newAggregateVersion) {
        var existingEventWithSameID = events.stream()
                .filter(e -> e.getId().equals(event.getId()))
                .findFirst()
                .orElse(null);

        if (existingEventWithSameID != null) {
            throw new DomainEventAlreadyPublished(event);
        }

        // An optimistic locking mechanism protecting aggregates from being updated with out-of-date events
        var existingAggregateVersion = getEventsByAggregateIdAndName(event.getAggregateId(), event.getAggregateName()).size();
        var aggregateVersionIsOutOfDate = existingAggregateVersion >= newAggregateVersion;

        if (aggregateVersionIsOutOfDate) {
            throw new AggregateVersionIsOutOfDate(event, newAggregateVersion);
        }

        try {
            eventPublisher.publish(event); // TODO: Let DB insert of event trigger the publishing to Kafka?
            eventStoreDAO.insert(event);

            events.add(event); // TODO: Remove
        } catch (JsonProcessingException e) {
            throw new DomainEventFailedToPublish(event, e);
        }
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
