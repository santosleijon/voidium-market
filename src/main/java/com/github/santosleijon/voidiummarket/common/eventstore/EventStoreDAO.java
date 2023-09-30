package com.github.santosleijon.voidiummarket.common.eventstore;

public interface EventStoreDAO {
    void insert(DomainEvent event);
    void deleteAll();
}
