package com.github.santosleijon.voidiummarket.purchaseorders.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PurchaseOrderPlaced extends DomainEvent {

    public static final String type = "PurchaseOrderPlaced";

    private final int unitsCount;
    private final BigDecimal pricePerUnit;
    private final Instant validTo;

    public PurchaseOrderPlaced(@JsonProperty("id") UUID id,
                               @JsonProperty("date") Instant date,
                               @JsonProperty("aggregateId") UUID aggregateId,
                               @JsonProperty("unitsCount") int unitsCount,
                               @JsonProperty("pricePerUnit") BigDecimal pricePerUnit,
                               @JsonProperty("validTo") Instant validTo) {
        super(id, date, type, PurchaseOrder.aggregateName, aggregateId);
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
