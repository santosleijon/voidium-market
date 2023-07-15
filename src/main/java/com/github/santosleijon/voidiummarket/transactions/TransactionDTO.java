package com.github.santosleijon.voidiummarket.transactions;

import java.util.UUID;

public record TransactionDTO(
        UUID id,
        UUID purchaseOrderId,
        UUID saleOrderId
) {
}
