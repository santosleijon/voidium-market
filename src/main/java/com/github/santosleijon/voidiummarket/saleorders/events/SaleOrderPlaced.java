package com.github.santosleijon.voidiummarket.saleorders.events;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

public class SaleOrderPlaced extends DomainEvent {

    private final int unitsCount;
    private final BigDecimal pricePerUnit;
    private final Currency currency;

    public SaleOrderPlaced(UUID id, Instant date, UUID aggregateId, int unitsCount, BigDecimal pricePerUnit, Currency currency) {
        super(id, date, SaleOrder.aggregateName, aggregateId);
        this.unitsCount = unitsCount;
        this.pricePerUnit = pricePerUnit;
        this.currency = currency;
    }

    public int getUnitsCount() {
        return unitsCount;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public Currency getCurrency() {
        return currency;
    }
}
