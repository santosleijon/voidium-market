package com.github.santosleijon.voidiummarket.saleorders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SaleOrderDTO(
        UUID id,
        int unitsCount,
        BigDecimal pricePerUnit,
        Instant validTo,
        boolean deleted
) {
}
