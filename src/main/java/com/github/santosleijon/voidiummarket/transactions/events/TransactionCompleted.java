package com.github.santosleijon.voidiummarket.transactions.events;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.transactions.Transaction;

import java.time.Instant;
import java.util.UUID;

public class TransactionCompleted extends DomainEvent {

    private final UUID purchaseOrderId;
    private final UUID saleOrderId;

    public TransactionCompleted(UUID id, UUID purchaseOrderId, UUID saleOrderId, Instant date) {
        super(id, date, Transaction.aggregateName, id);

        this.purchaseOrderId = purchaseOrderId;
        this.saleOrderId = saleOrderId;
    }

    public UUID getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public UUID getSaleOrderId() {
        return saleOrderId;
    }
}
