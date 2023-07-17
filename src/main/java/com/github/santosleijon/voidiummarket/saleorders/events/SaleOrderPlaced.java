package com.github.santosleijon.voidiummarket.saleorders.events;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class SaleOrderPlaced extends DomainEvent {

    private final int unitsCount;
    private final BigDecimal pricePerUnit;
    private final Instant validTo;

    public SaleOrderPlaced(UUID id, Instant date, UUID aggregateId, int unitsCount, BigDecimal pricePerUnit, Instant validTo) {
        super(id, date, "SaleOrderPlaced", SaleOrder.aggregateName, aggregateId);
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
