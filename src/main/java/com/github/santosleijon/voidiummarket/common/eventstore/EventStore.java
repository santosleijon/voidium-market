package com.github.santosleijon.voidiummarket.common.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.santosleijon.voidiummarket.common.eventstore.errors.AggregateVersionIsOutOfDate;
import com.github.santosleijon.voidiummarket.common.eventstore.errors.DomainEventAlreadyPublished;
import com.github.santosleijon.voidiummarket.common.eventstore.errors.DomainEventFailedToPublish;
import com.github.santosleijon.voidiummarket.common.eventstreaming.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;

@Component
public class EventStore {

    private final EventStoreDAO eventStoreDAO;

    private final EventPublisher eventPublisher;

    @Autowired
    public EventStore(EventStoreDAO eventStoreDAO, EventPublisher eventPublisher) {
        this.eventStoreDAO = eventStoreDAO;
        this.eventPublisher = eventPublisher;
    }

    public void publish(DomainEvent event, int newAggregateVersion) {
        var existingEventWithSameID = eventStoreDAO.getByEventId(event.getId());

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
        } catch (JsonProcessingException e) {
            throw new DomainEventFailedToPublish(event, e);
        }
    }

    public List<DomainEvent> getEventsByAggregateIdAndName(UUID aggregateId, String aggregateName) {
        return eventStoreDAO.getByAggregateIdAndName(aggregateId, aggregateName);
    }

    public Map<UUID, List<DomainEvent>> getEventsByAggregateName(String aggregateName) {
        return eventStoreDAO.getByAggregateName(aggregateName)
                .stream()
                .collect(groupingBy(DomainEvent::getAggregateId));
    }

    public void clear() {
        eventStoreDAO.deleteAll();
    }
}
