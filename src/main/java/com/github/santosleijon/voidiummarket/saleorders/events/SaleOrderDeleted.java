package com.github.santosleijon.voidiummarket.saleorders.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrder;

import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class SaleOrderDeleted extends DomainEvent {

    @JsonCreator
    public SaleOrderDeleted(
            @JsonProperty("id") UUID id,
            @JsonProperty("date") Instant date,
            @JsonProperty("aggregateId") UUID aggregateId) {
        super(id, date, "SaleOrderDeleted", SaleOrder.aggregateName, aggregateId);
    }
}
