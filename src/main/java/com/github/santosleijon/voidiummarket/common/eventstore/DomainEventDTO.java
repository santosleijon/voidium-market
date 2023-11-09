package com.github.santosleijon.voidiummarket.common.eventstore;

import com.github.santosleijon.voidiummarket.common.UUIDUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record DomainEventDTO(
        UUID id,
        String type,
        Instant date,
        String aggregateName,
        UUID aggregateId,
        Instant published,
        String data
) {
    public String formattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        return formatter.format(date);
    }

    public String formattedId() {
        return UUIDUtil.shorten(id);
    }

    public String formattedAggregateId() {
        return UUIDUtil.shorten(id);
    }
}
