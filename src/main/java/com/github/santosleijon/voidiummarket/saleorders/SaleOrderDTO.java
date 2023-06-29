package com.github.santosleijon.voidiummarket.saleorders;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public record SaleOrderDTO(
        UUID id,
        int unitsCount,
        BigDecimal pricePerUnit,
        Currency currency,
        boolean deleted
) {
}
