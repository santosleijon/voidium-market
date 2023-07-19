package com.github.santosleijon.voidiummarket.common.eventstore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class EventSubscriber {

    protected String eventType;

    @Autowired
    public EventSubscriber(String eventType, EventPublisher eventPublisher) {
        this.eventType = eventType;
        eventPublisher.registerPublisher(eventType, this);
    }

    public abstract void receive(DomainEvent event);
}
