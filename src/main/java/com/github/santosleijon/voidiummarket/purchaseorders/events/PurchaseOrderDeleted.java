package com.github.santosleijon.voidiummarket.purchaseorders.events;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;

import java.time.Instant;
import java.util.UUID;

public class PurchaseOrderDeleted extends DomainEvent {

    public PurchaseOrderDeleted(UUID id, Instant date, UUID aggregateId) {
        super(id, date, "PurchaseOrderDeleted", PurchaseOrder.aggregateName, aggregateId);
    }
}
