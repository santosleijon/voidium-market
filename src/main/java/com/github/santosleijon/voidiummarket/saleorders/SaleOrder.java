package com.github.santosleijon.voidiummarket.saleorders;

import com.github.santosleijon.voidiummarket.common.AggregateRoot;
import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.common.eventstore.errors.UnexpectedDomainEvent;
import com.github.santosleijon.voidiummarket.saleorders.events.SaleOrderDeleted;
import com.github.santosleijon.voidiummarket.saleorders.events.SaleOrderPlaced;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SaleOrder extends AggregateRoot {

    public final static String aggregateName = "SaleOrder";

    public int unitsCount;
    public BigDecimal pricePerUnit;
    public Currency currency;
    public boolean deleted;

    public SaleOrder(UUID id, Instant placedDate, int unitsCount, BigDecimal pricePerUnit, Currency currency) {
        super(aggregateName, id);

        var initEvent = new SaleOrderPlaced(id, placedDate, id, unitsCount, pricePerUnit, currency);
        this.apply(initEvent);
    }

    public SaleOrder(UUID id, List<DomainEvent> events) {
        super(aggregateName, id);
        events.forEach(this::mutate);
    }

    public void delete() {
        var eventId = UUID.randomUUID();
        var removedDate = Instant.now();

        var event = new SaleOrderDeleted(eventId, removedDate, id);
        this.apply(event);
    }

    @Override
    public void mutate(DomainEvent event) {
        if (event instanceof SaleOrderPlaced saleOrderPlaced) {
            this.id = saleOrderPlaced.getId();
            this.unitsCount = saleOrderPlaced.getUnitsCount();
            this.pricePerUnit = saleOrderPlaced.getPricePerUnit();
            this.currency = saleOrderPlaced.getCurrency();
            this.deleted = false;
        } else if (event instanceof SaleOrderDeleted) {
            this.deleted = true;
        } else {
            throw new UnexpectedDomainEvent(event);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleOrder saleOrder = (SaleOrder) o;
        return id == saleOrder.id && unitsCount == saleOrder.unitsCount && deleted == saleOrder.deleted && pricePerUnit.equals(saleOrder.pricePerUnit) && currency.equals(saleOrder.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unitsCount, pricePerUnit, currency, deleted);
    }
}
