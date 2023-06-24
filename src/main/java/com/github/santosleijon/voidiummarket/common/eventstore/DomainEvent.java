package com.github.santosleijon.voidiummarket.common.eventstore;

import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class DomainEvent {

    private final UUID id;

    private final Instant date;

    @Nullable
    private final String aggregateName;

    @Nullable
    private final UUID aggregateId;

    public DomainEvent(UUID id, Instant date, @Nullable String aggregateName, @Nullable UUID aggregateId) {
        this.id = id;
        this.date = date;
        this.aggregateName = aggregateName;
        this.aggregateId = aggregateId;
    }

    public UUID getId() {
        return id;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DomainEvent) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.date, that.date) &&
                Objects.equals(this.aggregateName, that.aggregateName) &&
                Objects.equals(this.aggregateId, that.aggregateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, aggregateName, aggregateId);
    }

    @Override
    public String toString() {
        return "DomainEvent[" +
                "id=" + id + ", " +
                "date=" + date + ", " +
                "aggregateName=" + aggregateName + ", " +
                "aggregateId=" + aggregateId + ']';
    }
}
