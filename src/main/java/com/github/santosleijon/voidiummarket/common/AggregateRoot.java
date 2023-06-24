package com.github.santosleijon.voidiummarket.common;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AggregateRoot {
    public String name;
    public UUID id;
    private final List<DomainEvent> pendingEvents = new ArrayList<>();

    public AggregateRoot(String name, UUID id) {
        this.name = name;
        this.id = id;
    }

    public void apply(DomainEvent event) {
        pendingEvents.add(event);
        mutate(event);
    }

    public abstract void mutate(DomainEvent event);

    public List<DomainEvent> getPendingEvents() {
        return pendingEvents;
    }
}
