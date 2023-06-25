package com.github.santosleijon.voidiummarket.common.eventstore.errors;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;

public class UnexpectedDomainEvent extends RuntimeException {

    public UnexpectedDomainEvent(DomainEvent event) {
        super("Unexpected event: " + event);
    }
}
