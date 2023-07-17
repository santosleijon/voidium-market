package com.github.santosleijon.voidiummarket.purchaseorders.events;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PurchaseOrderPlaced extends DomainEvent {

    private final int unitsCount;
    private final BigDecimal pricePerUnit;
    private final Instant validTo;

    public PurchaseOrderPlaced(UUID id, Instant date, UUID aggregateId, int unitsCount, BigDecimal pricePerUnit, Instant validTo) {
        super(id, date, "PurchaseOrderPlaced", PurchaseOrder.aggregateName, aggregateId);
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
