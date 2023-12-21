package com.github.santosleijon.voidiummarket.purchaseorders.projections;

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
public class PurchaseOrderProjectionsDAOImpl implements PurchaseOrderProjectionsDAO {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    public PurchaseOrderProjectionsDAOImpl(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void upsert(PurchaseOrderProjection purchaseOrder) {
        try {
            var data = objectMapper.writeValueAsString(purchaseOrder);

            Map<String, Object> paramMap = Map.of(
                    "purchase_order_id", purchaseOrder.getId(),
                    "data", data
            );

            jdbcTemplate.update("""
                        INSERT INTO purchase_order_projections (
                            purchase_order_id,
                            data
                        )
                        VALUES (
                            :purchase_order_id,
                            :data::jsonb
                        )
                        ON CONFLICT (purchase_order_id)
                        DO UPDATE SET
                            data = :data::jsonb
                    """.trim(), paramMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PurchaseOrderProjection get(UUID purchaseOrderId) {
        Map<String, Object> paramMap = Map.of("purchase_order_id", purchaseOrderId);

        var purchaseOrders = jdbcTemplate.query("""
                            SELECT
                                data
                            FROM
                                purchase_order_projections
                            WHERE
                                purchase_order_id = :purchase_order_id
                            LIMIT 1
                """.trim(), paramMap, new PurchaseOrderProjectionRowMapper(objectMapper));

        if (purchaseOrders.isEmpty()) {
            return null;
        }

        return purchaseOrders.get(0);
    }

    @Override
    public List<PurchaseOrderProjection> getNonDeleted() {
        return jdbcTemplate.query("""
                            SELECT
                                data
                            FROM
                                purchase_order_projections
                            WHERE
                                (data->>'deleted')::boolean IS FALSE
                """.trim(), Collections.emptyMap(), new PurchaseOrderProjectionRowMapper(objectMapper));
    }

    @Override
    public List<PurchaseOrderProjection> getUnfulfilled() {
        return jdbcTemplate.query("""
                            SELECT
                                data
                            FROM
                                purchase_order_projections
                            WHERE
                                data->>'fulfillmentStatus' = 'UNFULFILLED'
                            ORDER BY
                                data->>'placedDate'
                """.trim(), Collections.emptyMap(), new PurchaseOrderProjectionRowMapper(objectMapper));
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM purchase_order_projections", Collections.emptyMap());
    }

    private static class PurchaseOrderProjectionRowMapper implements RowMapper<PurchaseOrderProjection> {

        private final ObjectMapper objectMapper;

        private PurchaseOrderProjectionRowMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public PurchaseOrderProjection mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            String jsonData = resultSet.getString("data");

            try {
                return objectMapper.readValue(jsonData, PurchaseOrderProjection.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
