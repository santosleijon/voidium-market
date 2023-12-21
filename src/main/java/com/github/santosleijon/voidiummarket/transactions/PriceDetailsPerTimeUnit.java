package com.github.santosleijon.voidiummarket.transactions;

import com.github.santosleijon.voidiummarket.common.TimeUtils;

import java.math.BigDecimal;
import java.time.Instant;

public record PriceDetailsPerTimeUnit(
        Instant time,
        BigDecimal openPrice,
        BigDecimal closePrice,
        BigDecimal highPrice,
        BigDecimal lowPrice
) {
    public String formattedTime() {
        return TimeUtils.getFormattedDate(time);
    }
}
