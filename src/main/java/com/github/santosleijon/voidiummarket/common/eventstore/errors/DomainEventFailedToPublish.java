package com.github.santosleijon.voidiummarket.common.eventstore.errors;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;

public class DomainEventFailedToPublish extends RuntimeException {
    public DomainEventFailedToPublish(DomainEvent event, Throwable cause) {
        super("Failed to publish " + event.getClass().getSimpleName() + " event with ID " + event.getId(), cause);
    }
}
