package com.github.santosleijon.voidiummarket.saleorders;

import com.github.santosleijon.voidiummarket.transactions.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SaleOrderBuilder {

    private UUID id = UUID.randomUUID();
    private int unitsCount = 1;
    private BigDecimal pricePerUnit = BigDecimal.ONE;
    private Instant placedDate = Instant.now();
    private Instant validTo = Instant.now().plusSeconds(5);
    private List<Transaction> transactions;

    public SaleOrderBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public SaleOrderBuilder withUnitsCount(int unitsCount) {
        this.unitsCount = unitsCount;
        return this;
    }

    public SaleOrderBuilder withPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
        return this;
    }

    public SaleOrderBuilder withPlacedDate(Instant placedDate) {
        this.placedDate = placedDate;
        return this;
    }

    public SaleOrderBuilder withValidTo(Instant validTo) {
        this.validTo = validTo;
        return this;
    }

    public SaleOrderBuilder withTransaction(Transaction transaction) {
        if (transactions == null) {
            transactions = new ArrayList<>();
        }

        transactions.add(transaction);

        return this;
    }

    public SaleOrder build() {
        return new SaleOrder(id, placedDate, unitsCount, pricePerUnit, validTo).setTransactions(transactions);
    }
}
