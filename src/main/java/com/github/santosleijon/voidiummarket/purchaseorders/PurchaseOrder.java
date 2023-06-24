package com.github.santosleijon.voidiummarket.purchaseorders;

import com.github.santosleijon.voidiummarket.common.AggregateRoot;
import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.purchaseorders.events.PurchaseOrderPlaced;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PurchaseOrder extends AggregateRoot {

    public final static String aggregateName = "PurchaseOrder";

    public int unitsCount;
    public BigDecimal pricePerUnit;
    public Currency currency;


    public PurchaseOrder(UUID id, Instant placedDate, int unitsCount, BigDecimal pricePerUnit, Currency currency) {
        super(aggregateName, id);

        var initEvent = new PurchaseOrderPlaced(id, placedDate, id, unitsCount, pricePerUnit, currency);
        this.apply(initEvent);
    }

    public PurchaseOrder(UUID id, List<DomainEvent> events) {
        super(aggregateName, id);
        events.forEach(this::mutate);
    }

    @Override
    public void mutate(DomainEvent event) {
        if (event instanceof PurchaseOrderPlaced purchaseOrderPlaced) {
            this.id = purchaseOrderPlaced.getId();
            this.unitsCount = purchaseOrderPlaced.getUnitsCount();
            this.pricePerUnit = purchaseOrderPlaced.getPricePerUnit();
            this.currency = purchaseOrderPlaced.getCurrency();
        } else {
            throw new IllegalStateException("Unexpected event: " + event);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseOrder that = (PurchaseOrder) o;
        return id == that.id &&
                unitsCount == that.unitsCount &&
                pricePerUnit.equals(that.pricePerUnit) &&
                currency.equals(that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, unitsCount, pricePerUnit, currency);
    }
}
