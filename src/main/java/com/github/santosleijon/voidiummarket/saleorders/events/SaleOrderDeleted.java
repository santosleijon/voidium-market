package com.github.santosleijon.voidiummarket.saleorders.events;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrder;

import java.time.Instant;
import java.util.UUID;

public class SaleOrderDeleted extends DomainEvent {

    public SaleOrderDeleted(UUID id, Instant date, UUID aggregateId) {
        super(id, date, SaleOrder.aggregateName, aggregateId);
    }
}
