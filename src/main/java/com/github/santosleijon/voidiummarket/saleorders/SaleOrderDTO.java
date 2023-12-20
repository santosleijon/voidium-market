package com.github.santosleijon.voidiummarket.saleorders;

import com.github.santosleijon.voidiummarket.common.FulfillmentStatus;
import com.github.santosleijon.voidiummarket.common.TimeUtils;
import com.github.santosleijon.voidiummarket.common.UUIDUtil;
import com.github.santosleijon.voidiummarket.saleorders.projections.SaleOrderProjection;
import com.github.santosleijon.voidiummarket.transactions.Transaction;
import com.github.santosleijon.voidiummarket.transactions.TransactionDTO;
import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SaleOrderDTO(
        UUID id,
        int unitsCount,
        BigDecimal pricePerUnit,
        Instant validTo,
        boolean deleted,
        FulfillmentStatus fulfillmentStatus,
        @Nullable List<TransactionDTO> transactions) {
    public SaleOrderDTO(SaleOrderProjection saleOrderProjection) {
        this(saleOrderProjection.getId(),
                saleOrderProjection.getUnitsCount(),
                saleOrderProjection.getPricePerUnit(),
                saleOrderProjection.getValidTo(),
                saleOrderProjection.isDeleted(),
                saleOrderProjection.getFulfillmentStatus(),
                saleOrderProjection.getTransactions().stream()
                        .map(Transaction::toDTO)
                        .toList());
    }

    public String formattedId() {
        return UUIDUtil.shorten(id);
    }

    public String formattedValidTo() {
        return TimeUtils.getFormattedDate(validTo);
    }
}
