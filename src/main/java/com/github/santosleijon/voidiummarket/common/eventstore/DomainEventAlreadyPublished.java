package com.github.santosleijon.voidiummarket.common.eventstore;

public class DomainEventAlreadyPublished extends Exception {
    public DomainEventAlreadyPublished(DomainEvent event) {
        super("DomainEvent with ID " + event.getId() + " already published to event store");
    }
}
