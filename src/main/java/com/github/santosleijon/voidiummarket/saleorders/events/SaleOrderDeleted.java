package com.github.santosleijon.voidiummarket.saleorders.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrder;

import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class SaleOrderDeleted extends DomainEvent {

    @JsonCreator
    public SaleOrderDeleted(UUID id, Instant date, UUID aggregateId) {
        super(id, date, "SaleOrderDeleted", SaleOrder.aggregateName, aggregateId);
    }
}
