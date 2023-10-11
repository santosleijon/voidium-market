package com.github.santosleijon.voidiummarket.common.eventstore;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        property = "@class"
)
public class DomainEvent {

    private final UUID id;

    private final String type;

    private final Instant date;

    @Nullable
    private final String aggregateName;

    @Nullable
    private final UUID aggregateId;

    public DomainEvent(UUID id, Instant date, String type, @Nullable String aggregateName, @Nullable UUID aggregateId) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.aggregateName = aggregateName;
        this.aggregateId = aggregateId;
    }

    public UUID getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Instant getDate() {
        return date;
    }

    @Nullable
    public String getAggregateName() {
        return aggregateName;
    }

    @Nullable
    public UUID getAggregateId() {
        return aggregateId;
    }
}
