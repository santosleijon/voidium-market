package com.github.santosleijon.voidiummarket.purchaseorders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.santosleijon.voidiummarket.common.AggregateRoot;
import com.github.santosleijon.voidiummarket.common.FulfillmentStatus;
import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.common.eventstore.errors.UnexpectedDomainEvent;
import com.github.santosleijon.voidiummarket.purchaseorders.events.PurchaseOrderDeleted;
import com.github.santosleijon.voidiummarket.purchaseorders.events.PurchaseOrderPlaced;
import com.github.santosleijon.voidiummarket.transactions.Transaction;
import com.github.santosleijon.voidiummarket.transactions.TransactionDTO;
import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class PurchaseOrder extends AggregateRoot {

    public final static String aggregateName = "PurchaseOrder";

    private int unitsCount;
    private BigDecimal pricePerUnit;
    private Instant placedDate;
    private Instant validTo;
    private boolean deleted;
    private FulfillmentStatus fulfillmentStatus;

    @Nullable
    private List<Transaction> transactions;

    @JsonCreator
    public PurchaseOrder(UUID id, Instant placedDate, int unitsCount, BigDecimal pricePerUnit, Instant validTo) {
        super(aggregateName, id, 1);

        var initEvent = new PurchaseOrderPlaced(id, placedDate, id, unitsCount, pricePerUnit.setScale(2, RoundingMode.HALF_UP), validTo);
        this.apply(initEvent);
    }

    public PurchaseOrder(UUID id, List<DomainEvent> events) {
        super(aggregateName, id, events.size());
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

    public FulfillmentStatus getFulfillmentStatus() {
        return FulfillmentStatus.fromOrderTransactions(transactions, unitsCount);
    }

    public boolean isValid() {
        return Instant.now().compareTo(validTo) <= 0;
    }

    @Nullable
    public List<Transaction> getTransactions() {
        return transactions;
    }

    public PurchaseOrder setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        return this;
    }

    public void delete() {
        var eventId = UUID.randomUUID();
        var removedDate = Instant.now();

        var event = new PurchaseOrderDeleted(eventId, removedDate, id);
        this.apply(event);
    }

    public PurchaseOrderDTO toDTO() {
        List<TransactionDTO> transactionDTOs = null;

        if (transactions != null) {
            transactionDTOs = transactions.stream()
                    .map(t -> new TransactionDTO(t.getId(), t.getPurchaseOrderId(), t.getSaleOrderId(), t.getUnitsCount(), t.getPricePerUnit()))
                    .collect(Collectors.toList());
        }

        return new PurchaseOrderDTO(
                id,
                unitsCount,
                pricePerUnit,
                validTo,
                deleted,
                getFulfillmentStatus(),
                transactionDTOs);
    }

    @Override
    public void mutate(DomainEvent event) {
        if (event instanceof PurchaseOrderPlaced purchaseOrderPlaced) {
            this.id = purchaseOrderPlaced.getId();
            this.unitsCount = purchaseOrderPlaced.getUnitsCount();
            this.pricePerUnit = purchaseOrderPlaced.getPricePerUnit();
            this.validTo = purchaseOrderPlaced.getValidTo();
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
        return getId().equals(that.getId()) && unitsCount == that.unitsCount && deleted == that.deleted && pricePerUnit.equals(that.pricePerUnit) && validTo.equals(that.validTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unitsCount, pricePerUnit, validTo, deleted);
    }
}
