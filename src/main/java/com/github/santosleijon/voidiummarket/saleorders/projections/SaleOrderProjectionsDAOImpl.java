package com.github.santosleijon.voidiummarket.saleorders.projections;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class SaleOrderProjectionsDAOImpl implements SaleOrderProjectionsDAO {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    public SaleOrderProjectionsDAOImpl(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void upsert(SaleOrderProjection saleOrder) {
        try {
            var data = objectMapper.writeValueAsString(saleOrder);

            Map<String, Object> paramMap = Map.of(
                    "sale_order_id", saleOrder.getId(),
                    "data", data
            );

            jdbcTemplate.update("""
                        INSERT INTO sale_order_projections (
                            sale_order_id,
                            data
                        )
                        VALUES (
                            :sale_order_id,
                            :data::jsonb
                        )
                        ON CONFLICT (sale_order_id)
                        DO UPDATE SET
                            data = :data::jsonb
                    """.trim(), paramMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SaleOrderProjection get(UUID saleOrderId) {
        Map<String, Object> paramMap = Map.of("sale_order_id", saleOrderId);

        var saleOrders = jdbcTemplate.query("""
                            SELECT
                                data
                            FROM
                                sale_order_projections
                            WHERE
                                sale_order_id = :sale_order_id
                            LIMIT 1
                """.trim(), paramMap, new SaleOrderProjectionRowMapper(objectMapper));

        if (saleOrders.isEmpty()) {
            return null;
        }

        return saleOrders.get(0);
    }

    @Override
    public List<SaleOrderProjection> getNonDeleted() {
        return jdbcTemplate.query("""
                            SELECT
                                data
                            FROM
                                sale_order_projections
                            WHERE
                                (data->>'deleted')::boolean IS FALSE
                """.trim(), Collections.emptyMap(), new SaleOrderProjectionRowMapper(objectMapper));
    }

    @Override
    public List<SaleOrderProjection> getUnfulfilled() {
        return jdbcTemplate.query("""
                            SELECT
                                data
                            FROM
                                sale_order_projections
                            WHERE
                                data->>'fulfillmentStatus' = 'UNFULFILLED'
                            ORDER BY
                                data->>'placedDate'
                """.trim(), Collections.emptyMap(), new SaleOrderProjectionRowMapper(objectMapper));
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM sale_order_projections", Collections.emptyMap());
    }

    private static class SaleOrderProjectionRowMapper implements RowMapper<SaleOrderProjection> {

        private final ObjectMapper objectMapper;

        private SaleOrderProjectionRowMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public SaleOrderProjection mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            String jsonData = resultSet.getString("data");

            try {
                return objectMapper.readValue(jsonData, SaleOrderProjection.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
