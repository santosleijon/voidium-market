package com.github.santosleijon.voidiummarket.common.eventstore;

import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.UUID;

public record DomainEvent(UUID id, Instant date, @Nullable UUID aggregateId) {}
