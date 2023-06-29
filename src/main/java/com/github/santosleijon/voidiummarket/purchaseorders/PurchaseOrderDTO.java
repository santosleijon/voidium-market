package com.github.santosleijon.voidiummarket.purchaseorders;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public record PurchaseOrderDTO(
        UUID id,
        int unitsCount,
        BigDecimal pricePerUnit,
        Currency currency,
        boolean deleted) {
}
