package com.github.santosleijon.voidiummarket.purchaseorders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PurchaseOrderDTO(
        UUID id,
        int unitsCount,
        BigDecimal pricePerUnit,
        Instant validTo,
        boolean deleted) {
}
