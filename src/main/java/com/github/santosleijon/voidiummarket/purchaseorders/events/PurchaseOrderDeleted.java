package com.github.santosleijon.voidiummarket.purchaseorders.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;

import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class PurchaseOrderDeleted extends DomainEvent {

    @JsonCreator
    public PurchaseOrderDeleted(
            @JsonProperty("id") UUID id,
            @JsonProperty("date") Instant date,
            @JsonProperty("aggregateId") UUID aggregateId) {
        super(id, date, "PurchaseOrderDeleted", PurchaseOrder.aggregateName, aggregateId);
    }
}
