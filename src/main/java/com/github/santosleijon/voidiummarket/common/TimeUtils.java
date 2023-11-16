package com.github.santosleijon.voidiummarket.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeUtils {

    private TimeUtils() {
        // Private constructor to prevent instantiation
    }

    public static LocalDateTime getZuluLocalDateTime(Instant date) {
        return LocalDateTime.ofInstant(date, ZoneId.of("Z"));
    }
}
