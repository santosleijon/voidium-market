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

    @Nullable
    private Instant published;

    public DomainEvent(UUID id, Instant date, String type, @Nullable String aggregateName, @Nullable UUID aggregateId, @Nullable Instant published) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.aggregateName = aggregateName;
        this.aggregateId = aggregateId;
        this.published = published;
    }

    public DomainEvent(UUID id, Instant date, String type, @Nullable String aggregateName, @Nullable UUID aggregateId) {
        this(id, date, type, aggregateName, aggregateId, null);
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

    @Nullable
    public Instant getPublished() {
        return published;
    }

    public void markAsPublished() {
        this.published = Instant.now();
    }
}
