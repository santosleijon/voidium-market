package com.github.santosleijon.voidiummarket.transactions;

import java.math.BigDecimal;

public record PriceDetailsPerTimeUnit(
        String time,
        BigDecimal openPrice,
        BigDecimal closePrice,
        BigDecimal highPrice,
        BigDecimal lowPrice
) {}
