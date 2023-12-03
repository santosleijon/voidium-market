package com.github.santosleijon.voidiummarket.common.eventstore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.santosleijon.voidiummarket.common.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class EventStoreDAOImpl implements EventStoreDAO {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public EventStoreDAOImpl(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public void insert(DomainEvent event) {
        try {
            var eventData = objectMapper.writeValueAsString(event);

            Map<String, Object> paramMap = Map.of(
                    "event_id", event.getId(),
                    "type", event.getType(),
                    "event_date", TimeUtils.getZuluLocalDateTime(event.getDate()),
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
                """.trim(), paramMap, new DomainEventRowMapper(objectMapper));

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
                            ORDER BY
                                event_date
                """.trim(), paramMap, new DomainEventRowMapper(objectMapper));
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
                """.trim(), paramMap, new DomainEventRowMapper(objectMapper));
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
                """.trim(), new DomainEventRowMapper(objectMapper));
    }

    @Override
    public List<DomainEventWithData> getPaginatedEventsWithData(int page, int eventsPerPage) {
        Map<String, Object> paramMap = Map.of(
                "limit", eventsPerPage,
                "offset", (page - 1) * eventsPerPage
        );

        return jdbcTemplate.query("""
                            SELECT
                                data
                            FROM
                                event_store
                            ORDER BY
                                event_date DESC
                            LIMIT :limit
                            OFFSET :offset
                """.trim(), paramMap, new DomainEventWithDataRowMapper(objectMapper));
    }

    @Override
    public int getEventsCount() {
        var result =  jdbcTemplate.queryForObject("""
                            SELECT
                                COUNT(*)
                            FROM
                                event_store
                """.trim(), Collections.emptyMap(), Integer.class);

        if (result == null) {
            return 0;
        }

        return result;
    }

    @Override
    public void markEventAsPublished(UUID eventId) {
        try {
            Map<String, Object> paramMap = Map.of(
                    "event_id", eventId,
                    "published_date", TimeUtils.getZuluLocalDateTime(Instant.now())
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

    private static class DomainEventRowMapper implements RowMapper<DomainEvent> {

        private final ObjectMapper objectMapper;

        private DomainEventRowMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

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

    private static class DomainEventWithDataRowMapper implements RowMapper<DomainEventWithData> {

        private final ObjectMapper objectMapper;

        private DomainEventWithDataRowMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public DomainEventWithData mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            String eventAsJsonString = resultSet.getString("data");

            try {
                var domainEvent = objectMapper.readValue(eventAsJsonString, DomainEvent.class);

                return new DomainEventWithData(domainEvent, eventAsJsonString);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
