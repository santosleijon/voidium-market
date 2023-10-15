package com.github.santosleijon.voidiummarket.common.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Override
    public DomainEvent getByEventId(UUID eventId) {
        Map<String, UUID> paramMap = Map.of("event_id", eventId);

        var events = jdbcTemplate.query("""
                            SELECT
                                data
                            FROM
                                event_store
                            WHERE
                                event_id = :event_id
                            LIMIT 1
                """.trim(), paramMap, new DomainEventRowMapper());

        if (events.isEmpty()) {
            return null;
        }

        return events.get(0);
    }

    @Override
    public List<DomainEvent> getByAggregateName(String aggregateName) {
        Map<String, String> paramMap = Map.of("aggregate_name", aggregateName);

        return jdbcTemplate.query("""
                            SELECT
                                data
                            FROM
                                event_store
                            WHERE
                                aggregate_name = :aggregate_name
                """.trim(), paramMap, new DomainEventRowMapper());
    }

    @Override
    public List<DomainEvent> getByAggregateIdAndName(UUID aggregateId, String aggregateName) {
        Map<String, Object> paramMap = Map.of(
                "aggregate_id", aggregateId,
                "aggregate_name", aggregateName
        );

        return jdbcTemplate.query("""
                            SELECT
                                data
                            FROM
                                event_store
                            WHERE
                                aggregate_id = :aggregate_id AND
                                aggregate_name = :aggregate_name
                            ORDER BY
                                event_date
                """.trim(), paramMap, new DomainEventRowMapper());
    }

    @Override
    public List<DomainEvent> getUnpublishedEvents() {
        return jdbcTemplate.query("""
                            SELECT
                                data
                            FROM
                                event_store
                            WHERE
                                published IS NULL
                            ORDER BY
                                event_date
                            LIMIT
                                100
                """.trim(), new DomainEventRowMapper());
    }

    @Override
    public void markEventAsPublished(UUID eventId) {
        try {
            Map<String, Object> paramMap = Map.of(
                    "event_id", eventId,
                    "published_date", getZuluLocalDateTime(Instant.now())
            );

            jdbcTemplate.update("""
                        UPDATE
                            event_store
                        SET
                           published = :published_date
                        WHERE
                            event_id = :event_id
                    """.trim(), paramMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("""
                    DELETE FROM event_store
                """.trim(), Collections.emptyMap());
    }

    private LocalDateTime getZuluLocalDateTime(Instant date) {
        return LocalDateTime.ofInstant(date, ZoneId.of("Z"));
    }

    private static class DomainEventRowMapper implements RowMapper<DomainEvent> {

        private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        @Override
        public DomainEvent mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            String eventAsJsonString = resultSet.getString("data");

            try {
                return objectMapper.readValue(eventAsJsonString, DomainEvent.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
