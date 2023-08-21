package com.github.santosleijon.voidiummarket.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AggregateRoot {

    protected final String name;

    protected UUID id;

    protected int currentVersion;

    @JsonIgnore
    private final List<DomainEvent> pendingEvents = new ArrayList<>();

    public AggregateRoot(String name, UUID id, int currentVersion) {
        this.name = name;
        this.id = id;
        this.currentVersion = currentVersion;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public int getCurrentVersion() {
        return currentVersion;
    }

    public void apply(DomainEvent event) {
        pendingEvents.add(event);
        mutate(event);
        currentVersion++;
    }

    public abstract void mutate(DomainEvent event);

    public List<DomainEvent> getPendingEvents() {
        return pendingEvents;
    }
}
