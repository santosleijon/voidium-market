package com.github.santosleijon.voidiummarket.transactions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.santosleijon.voidiummarket.common.AggregateRoot;
import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.common.eventstore.errors.UnexpectedDomainEvent;
import com.github.santosleijon.voidiummarket.transactions.events.TransactionCompleted;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Transaction extends AggregateRoot {

    public final static String aggregateName = "Transaction";

    private UUID purchaseOrderId;
    private UUID saleOrderId;
    private int unitsCount;
    private BigDecimal pricePerUnit;
    private Instant date;

    @JsonCreator
    public Transaction(
            @JsonProperty("id") UUID id,
            @JsonProperty("purchaseOrderId") UUID purchaseOrderId,
            @JsonProperty("saleOrderId") UUID saleOrderId,
            @JsonProperty("unitsCount") int unitsCount,
            @JsonProperty("pricePerUnit") BigDecimal pricePerUnit,
            @JsonProperty("date") Instant date
    ) {
        super(aggregateName, id, 0);

        var initEvent = new TransactionCompleted(id, purchaseOrderId, saleOrderId, unitsCount, pricePerUnit, date);
        this.apply(initEvent);
    }

    public Transaction(UUID id, List<DomainEvent> events) {
        super(aggregateName, id, events.size());
        events.forEach(this::mutate);
    }

    public UUID getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public UUID getSaleOrderId() {
        return saleOrderId;
    }

    public Instant getDate() {
        return date;
    }

    public int getUnitsCount() {
        return unitsCount;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    @Override
    public void mutate(DomainEvent event) {
        if (event instanceof TransactionCompleted transactionCompleted) {
            this.id = transactionCompleted.getId();
            this.purchaseOrderId = transactionCompleted.getPurchaseOrderId();
            this.saleOrderId = transactionCompleted.getSaleOrderId();
            this.unitsCount = transactionCompleted.getUnitsCount();
            this.pricePerUnit = transactionCompleted.getPricePerUnit();
            this.date = transactionCompleted.getDate();
        } else {
            throw new UnexpectedDomainEvent(event);
        }
    }

    public TransactionDTO toDTO() {
        return new TransactionDTO(getId(), getPurchaseOrderId(), getSaleOrderId(), getDate(), getUnitsCount(), getPricePerUnit());
    }
}
