package com.github.santosleijon.voidiummarket.common.eventstore;

public class DomainEventWithData extends DomainEvent {

    private final String data;

    public DomainEventWithData(DomainEvent event, String data) {
        super(event.getId(), event.getDate(), event.getType(), event.getAggregateName(), event.getAggregateId(), event.getPublished());
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
