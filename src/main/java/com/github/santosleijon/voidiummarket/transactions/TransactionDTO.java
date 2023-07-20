package com.github.santosleijon.voidiummarket.transactions;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionDTO(
        UUID id,
        UUID purchaseOrderId,
        UUID saleOrderId,
        int unitsCount,
        BigDecimal pricePerUnit
) {
}
