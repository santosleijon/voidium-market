package com.github.santosleijon.voidiummarket.saleorders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.santosleijon.voidiummarket.common.AggregateRoot;
import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.common.eventstore.errors.UnexpectedDomainEvent;
import com.github.santosleijon.voidiummarket.saleorders.events.SaleOrderDeleted;
import com.github.santosleijon.voidiummarket.saleorders.events.SaleOrderPlaced;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SaleOrder extends AggregateRoot {

    public final static String aggregateName = "SaleOrder";

    private int unitsCount;
    private BigDecimal pricePerUnit;
    private Instant placedDate;
    private Instant validTo;
    private boolean deleted;

    @JsonCreator
    public SaleOrder(UUID id, Instant placedDate, int unitsCount, BigDecimal pricePerUnit, Instant validTo) {
        super(aggregateName, id);

        var initEvent = new SaleOrderPlaced(id, placedDate, id, unitsCount, pricePerUnit.setScale(2, RoundingMode.HALF_UP), validTo);
        this.apply(initEvent);
    }

    public SaleOrder(UUID id, List<DomainEvent> events) {
        super(aggregateName, id);
        events.forEach(this::mutate);
    }

    public int getUnitsCount() {
        return unitsCount;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public Instant getPlacedDate() {
        return placedDate;
    }

    public Instant getValidTo() {
        return validTo;
    }

    public boolean isDeleted() {
        return deleted;
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
            this.placedDate = saleOrderPlaced.getDate();
            this.validTo = saleOrderPlaced.getValidTo();
            this.deleted = false;
        } else if (event instanceof SaleOrderDeleted) {
            this.deleted = true;
        } else {
            throw new UnexpectedDomainEvent(event);
        }
    }

    public SaleOrderDTO toDTO() {
        return new SaleOrderDTO(
                this.id,
                this.unitsCount,
                this.pricePerUnit,
                this.validTo,
                this.deleted
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleOrder saleOrder = (SaleOrder) o;
        return id.equals(saleOrder.id) && unitsCount == saleOrder.unitsCount && deleted == saleOrder.deleted && pricePerUnit.equals(saleOrder.pricePerUnit) && validTo.equals(saleOrder.validTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unitsCount, pricePerUnit, validTo, deleted);
    }
}
