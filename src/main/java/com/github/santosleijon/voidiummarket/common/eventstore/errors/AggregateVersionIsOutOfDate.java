package com.github.santosleijon.voidiummarket.common.eventstore.errors;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;

public class AggregateVersionIsOutOfDate extends RuntimeException {
    public AggregateVersionIsOutOfDate(DomainEvent event, int newAggregateVersion) {
        super("Version " + newAggregateVersion + " of aggregate " + event.getAggregateName() + " with ID " + event.getAggregateId() + " has already been saved");
    }
}
