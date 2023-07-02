package com.github.santosleijon.voidiummarket.common.eventstore.errors;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;

public class DomainEventAlreadyPublished extends RuntimeException {
    public DomainEventAlreadyPublished(DomainEvent event) {
        super("DomainEvent with ID " + event.getId() + " already published to event store");
    }
}
