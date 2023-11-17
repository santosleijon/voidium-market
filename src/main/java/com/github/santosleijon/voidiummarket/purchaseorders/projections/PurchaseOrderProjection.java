package com.github.santosleijon.voidiummarket.purchaseorders.projections;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.santosleijon.voidiummarket.common.FulfillmentStatus;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;
import com.github.santosleijon.voidiummarket.transactions.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class PurchaseOrderProjection {
    private UUID id;
    private int currentVersion;
    private Instant placedDate;
    private int unitsCount;
    private BigDecimal pricePerUnit;
    private Instant validTo;
    private FulfillmentStatus fulfillmentStatus;
    private boolean deleted;
    private List<Transaction> transactions;

    @JsonCreator
    public PurchaseOrderProjection(
            @JsonProperty("id") UUID id,
            @JsonProperty("currentVersion") int currentVersion,
            @JsonProperty("placedDate") Instant placedDate,
            @JsonProperty("unitsCount") int unitsCount,
            @JsonProperty("pricePerUnit") BigDecimal pricePerUnit,
            @JsonProperty("validTo") Instant validTo,
            @JsonProperty("fulfillmentStatus") FulfillmentStatus fulfillmentStatus,
            @JsonProperty("deleted") boolean deleted,
            @JsonProperty("transactions") List<Transaction> transactions
    ) {
        this.id = id;
        this.currentVersion = currentVersion;
        this.placedDate = placedDate;
        this.unitsCount = unitsCount;
        this.pricePerUnit = pricePerUnit;
        this.validTo = validTo;
        this.fulfillmentStatus = fulfillmentStatus;
        this.deleted = deleted;
        this.transactions = transactions;
    }

    public PurchaseOrderProjection(PurchaseOrder purchaseOrder) {
        this(purchaseOrder.getId(),
                purchaseOrder.getCurrentVersion(),
                purchaseOrder.getPlacedDate(),
                purchaseOrder.getUnitsCount(),
                purchaseOrder.getPricePerUnit(),
                purchaseOrder.getValidTo(),
                purchaseOrder.getFulfillmentStatus(),
                purchaseOrder.isDeleted(),
                purchaseOrder.getTransactions());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(int currentVersion) {
        this.currentVersion = currentVersion;
    }

    public Instant getPlacedDate() {
        return placedDate;
    }

    public void setPlacedDate(Instant placedDate) {
        this.placedDate = placedDate;
    }

    public int getUnitsCount() {
        return unitsCount;
    }

    public void setUnitsCount(int unitsCount) {
        this.unitsCount = unitsCount;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Instant getValidTo() {
        return validTo;
    }

    public void setValidTo(Instant validTo) {
        this.validTo = validTo;
    }

    public FulfillmentStatus getFulfillmentStatus() {
        return fulfillmentStatus;
    }

    public void setFulfillmentStatus(FulfillmentStatus fulfillmentStatus) {
        this.fulfillmentStatus = fulfillmentStatus;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @JsonIgnore
    public boolean isValid() {
        return Instant.now().compareTo(validTo) <= 0;
    }
}
