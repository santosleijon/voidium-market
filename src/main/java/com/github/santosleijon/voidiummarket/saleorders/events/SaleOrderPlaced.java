package com.github.santosleijon.voidiummarket.saleorders.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class SaleOrderPlaced extends DomainEvent {

    public final static String type = "SaleOrderPlaced";

    private final int unitsCount;
    private final BigDecimal pricePerUnit;
    private final Instant validTo;

    @JsonCreator
    public SaleOrderPlaced(@JsonProperty("id") UUID id,
                           @JsonProperty("date") Instant date,
                           @JsonProperty("aggregateId") UUID aggregateId,
                           @JsonProperty("unitsCount") int unitsCount,
                           @JsonProperty("pricePerUnit") BigDecimal pricePerUnit,
                           @JsonProperty("validTo") Instant validTo) {
        super(id, date, type, SaleOrder.aggregateName, aggregateId);
        this.unitsCount = unitsCount;
        this.pricePerUnit = pricePerUnit;
        this.validTo = validTo;
    }

    public int getUnitsCount() {
        return unitsCount;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public Instant getValidTo() {
        return validTo;
    }
}
