package com.github.santosleijon.voidiummarket.common.eventstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;

@Component
public class EventStoreDAOImpl implements EventStoreDAO {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    public EventStoreDAOImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(DomainEvent event) {
        try {
            var eventData = objectMapper.writeValueAsString(event);

            Map<String, Object> paramMap = Map.of(
                    "event_id", event.getId(),
                    "type", event.getType(),
                    "event_date", getZuluLocalDateTime(event.getDate()),
                    "aggregate_name", event.getAggregateName(),
                    "aggregate_id", event.getAggregateId(),
                    "data", eventData
            );

            jdbcTemplate.update("""
                INSERT INTO event_store (
                    event_id,
                    type,
                    event_date,
                    aggregate_name,
                    aggregate_id,
                    data
                )
                VALUES (
                    :event_id,
                    :type,
                    :event_date,
                    :aggregate_name,
                    :aggregate_id,
                    :data::jsonb
                )
            """.trim(), paramMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: Retrieve events

    // TODO: Remove?
    public void deleteAll() {
        jdbcTemplate.update("""
            DELETE FROM event_store
        """.trim(), Collections.emptyMap());
    }

    private LocalDateTime getZuluLocalDateTime(Instant date) {
        return LocalDateTime.ofInstant(date, ZoneId.of("Z"));
    }
}
