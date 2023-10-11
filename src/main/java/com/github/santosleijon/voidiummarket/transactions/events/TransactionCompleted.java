package com.github.santosleijon.voidiummarket.transactions.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.transactions.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class TransactionCompleted extends DomainEvent {

    private final UUID purchaseOrderId;
    private final UUID saleOrderId;
    private final int unitsCount;
    private final BigDecimal pricePerUnit;

    @JsonCreator
    public TransactionCompleted(
            @JsonProperty("id") UUID id,
            @JsonProperty("purchaseOrderId") UUID purchaseOrderId,
            @JsonProperty("saleOrderId") UUID saleOrderId,
            @JsonProperty("unitsCount") int unitsCount,
            @JsonProperty("pricePerUnit") BigDecimal pricePerUnit,
            @JsonProperty("date") Instant date
    ) {
        super(id, date, "TransactionCompleted", Transaction.aggregateName, id);

        this.purchaseOrderId = purchaseOrderId;
        this.saleOrderId = saleOrderId;
        this.unitsCount = unitsCount;
        this.pricePerUnit = pricePerUnit.setScale(2, RoundingMode.HALF_UP);
    }

    public UUID getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public UUID getSaleOrderId() {
        return saleOrderId;
    }

    public int getUnitsCount() {
        return unitsCount;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }
}
