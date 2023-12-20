package com.github.santosleijon.voidiummarket.transactions;

import com.github.santosleijon.voidiummarket.common.UUIDUtil;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record TransactionDTO(
        UUID id,
        UUID purchaseOrderId,
        UUID saleOrderId,
        Instant date,
        int unitsCount,
        BigDecimal pricePerUnit
) {
    public String formattedId() {
        return UUIDUtil.shorten(id);
    }

    public String formattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        return formatter.format(date);
    }
}
