package com.github.santosleijon.voidiummarket.mocks;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.common.eventstore.EventStoreDAO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class EventStoreDAOMock implements EventStoreDAO {

    private final List<DomainEvent> events = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void insert(DomainEvent event) {
        events.add(event);
    }

    @Override
    public DomainEvent getByEventId(UUID eventId) {
        return events.stream()
                .filter(e -> e.getId().equals(eventId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<DomainEvent> getByAggregateName(String aggregateName) {
        return events.stream()
                .filter(e -> e.getAggregateName().equals(aggregateName))
                .collect(Collectors.toList());
    }

    @Override
    public List<DomainEvent> getByAggregateIdAndName(UUID aggregateId, String aggregateName) {
        return events.stream()
                .filter(e -> e.getAggregateId().equals(aggregateId) && e.getAggregateName().equals(aggregateName))
                .collect(Collectors.toList());
    }

    @Override
    public List<DomainEvent> getUnpublishedEvents() {
        return events.stream()
                .filter(e -> e.getPublished() == null)
                .collect(Collectors.toList());
    }

    @Override
    public void markEventAsPublished(UUID eventId) {
        var optionalEvent = events.stream()
                .filter(e -> e.getId().equals(eventId))
                .findFirst();

        if (optionalEvent.isEmpty()) {
            return;
        }

        var event = optionalEvent.get();

        event.markAsPublished();
    }

    @Override
    public void deleteAll() {
        events.clear();
    }
}
