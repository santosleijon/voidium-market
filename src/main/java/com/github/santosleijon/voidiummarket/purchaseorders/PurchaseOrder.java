package com.github.santosleijon.voidiummarket.purchaseorders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.santosleijon.voidiummarket.common.AggregateRoot;
import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.common.eventstore.errors.UnexpectedDomainEvent;
import com.github.santosleijon.voidiummarket.purchaseorders.events.PurchaseOrderPlaced;
import com.github.santosleijon.voidiummarket.purchaseorders.events.PurchaseOrderDeleted;

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
    public Instant placedDate;
    public boolean deleted;

    @JsonCreator
    public PurchaseOrder(UUID id, Instant placedDate, int unitsCount, BigDecimal pricePerUnit, Currency currency) {
        super(aggregateName, id);

        var initEvent = new PurchaseOrderPlaced(id, placedDate, id, unitsCount, pricePerUnit, currency);
        this.apply(initEvent);
    }

    public PurchaseOrder(UUID id, List<DomainEvent> events) {
        super(aggregateName, id);
        events.forEach(this::mutate);
    }

    public void delete() {
        var eventId = UUID.randomUUID();
        var removedDate = Instant.now();

        var event = new PurchaseOrderDeleted(eventId, removedDate, id);
        this.apply(event);
    }

    public PurchaseOrderDTO toDTO() {
        return new PurchaseOrderDTO(
                id,
                unitsCount,
                pricePerUnit,
                currency,
                deleted);
    }

    @Override
    public void mutate(DomainEvent event) {
        if (event instanceof PurchaseOrderPlaced purchaseOrderPlaced) {
            this.id = purchaseOrderPlaced.getId();
            this.unitsCount = purchaseOrderPlaced.getUnitsCount();
            this.pricePerUnit = purchaseOrderPlaced.getPricePerUnit();
            this.currency = purchaseOrderPlaced.getCurrency();
            this.placedDate = purchaseOrderPlaced.getDate();
            this.deleted = false;
        } else if (event instanceof PurchaseOrderDeleted) {
            this.deleted = true;
        } else {
            throw new UnexpectedDomainEvent(event);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PurchaseOrder that = (PurchaseOrder) o;
        return id.equals(that.id) && unitsCount == that.unitsCount && deleted == that.deleted && pricePerUnit.equals(that.pricePerUnit) && currency.equals(that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unitsCount, pricePerUnit, currency, deleted);
    }
}
