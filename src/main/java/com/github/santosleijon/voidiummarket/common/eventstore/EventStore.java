package com.github.santosleijon.voidiummarket.common.eventstore;

import com.github.santosleijon.voidiummarket.common.eventstore.errors.AggregateVersionIsOutOfDate;
import com.github.santosleijon.voidiummarket.common.eventstore.errors.DomainEventAlreadyPublished;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;

@Component
public class EventStore {

    private final EventStoreDAO eventStoreDAO;

    @Autowired
    public EventStore(EventStoreDAO eventStoreDAO) {
        this.eventStoreDAO = eventStoreDAO;
    }

    public void append(DomainEvent event, int newAggregateVersion) {
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

        eventStoreDAO.insert(event);
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
