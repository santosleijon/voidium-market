package com.github.santosleijon.voidiummarket.purchaseorders;

import com.github.santosleijon.voidiummarket.transactions.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PurchaseOrderBuilder {

    private UUID id = UUID.randomUUID();
    private int unitsCount = 1;
    private BigDecimal pricePerUnit = BigDecimal.ONE;
    private Instant placedDate = Instant.now();
    private Instant validTo = Instant.now().plusSeconds(5);
    private List<Transaction> transactions;

    public PurchaseOrderBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public PurchaseOrderBuilder withUnitsCount(int unitsCount) {
        this.unitsCount = unitsCount;
        return this;
    }

    public PurchaseOrderBuilder withPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        return this;
    }

    public PurchaseOrderBuilder withPlacedDate(Instant placedDate) {
        this.placedDate = placedDate;
        return this;
    }

    public PurchaseOrderBuilder withValidTo(Instant validTo) {
        this.validTo = validTo;
        return this;
    }

    public PurchaseOrderBuilder withTransaction(Transaction transaction) {
        if (transactions == null) {
            transactions = new ArrayList<>();
        }

        transactions.add(transaction);

        return this;
    }

    public PurchaseOrder build() {
        return new PurchaseOrder(id, placedDate, unitsCount, pricePerUnit, validTo).setTransactions(transactions);
    }
}
