package com.github.santosleijon.voidiummarket.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    private TimeUtils() {
        // Private constructor to prevent instantiation
    }

    public static LocalDateTime getZuluLocalDateTime(Instant date) {
        return LocalDateTime.ofInstant(date, ZoneId.of("Z"));
    }

    public static String getFormattedDate(Instant date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        return formatter.format(date);
    }
}
