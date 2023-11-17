package com.github.santosleijon.voidiummarket.purchaseorders;

import com.github.santosleijon.voidiummarket.common.FulfillmentStatus;
import com.github.santosleijon.voidiummarket.purchaseorders.projections.PurchaseOrderProjection;
import com.github.santosleijon.voidiummarket.transactions.Transaction;
import com.github.santosleijon.voidiummarket.transactions.TransactionDTO;
import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PurchaseOrderDTO(
        UUID id,
        int unitsCount,
        BigDecimal pricePerUnit,
        Instant validTo,
        boolean deleted,
        FulfillmentStatus fulfillmentStatus,
        @Nullable List<TransactionDTO> transactions
) {
    public PurchaseOrderDTO(PurchaseOrderProjection purchaseOrderProjection) {
        this(purchaseOrderProjection.getId(),
                purchaseOrderProjection.getUnitsCount(),
                purchaseOrderProjection.getPricePerUnit(),
                purchaseOrderProjection.getValidTo(),
                purchaseOrderProjection.isDeleted(),
                purchaseOrderProjection.getFulfillmentStatus(),
                purchaseOrderProjection.getTransactions().stream()
                        .map(Transaction::toDTO)
                        .toList());
    }
}
