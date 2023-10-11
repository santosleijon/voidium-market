package com.github.santosleijon.voidiummarket.common.eventstore;

import java.util.List;
import java.util.UUID;

public interface EventStoreDAO {
    void insert(DomainEvent event);
    DomainEvent getByEventId(UUID eventId);
    List<DomainEvent> getByAggregateName(String aggregateName);
    List<DomainEvent> getByAggregateIdAndName(UUID aggregateId, String aggregateName);
    void deleteAll();
}
