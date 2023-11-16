package com.github.santosleijon.voidiummarket.purchaseorders.projections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.santosleijon.voidiummarket.common.TimeUtils;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class PurchaseOrderProjectionsDAOImpl implements PurchaseOrderProjectionsDAO {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    public PurchaseOrderProjectionsDAOImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void upsert(PurchaseOrder purchaseOrder) {
        try {
            var data = objectMapper.writeValueAsString(purchaseOrder);

            Map<String, Object> paramMap = Map.of(
                    "purchase_order_id", purchaseOrder.getId(),
                    "units_count", purchaseOrder.getUnitsCount(),
                    "price_per_unit", purchaseOrder.getPricePerUnit(),
                    "valid_to", TimeUtils.getZuluLocalDateTime(purchaseOrder.getValidTo()),
                    "fulfillment_status", purchaseOrder.getFulfillmentStatus().name(),
                    "data", data,
                    "version", purchaseOrder.getCurrentVersion()
            );

            jdbcTemplate.update("""
                        INSERT INTO purchase_order_projections (
                            purchase_order_id,
                            units_count,
                            price_per_unit,
                            valid_to,
                            fulfillment_status,
                            data,
                            version
                        )
                        VALUES (
                            :purchase_order_id,
                            :units_count,
                            :price_per_unit,
                            :valid_to,
                            :fulfillment_status,
                            :data::jsonb,
                            :version
                        )
                        ON CONFLICT (purchase_order_id)
                        DO UPDATE SET
                            units_count = :units_count,
                            price_per_unit = :price_per_unit,
                            valid_to = :valid_to,
                            fulfillment_status = :fulfillment_status,
                            data = :data::jsonb,
                            version = :version;
                    """.trim(), paramMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UUID purchaseOrderId) {
        Map<String, Object> paramMap = Map.of("purchase_order_id", purchaseOrderId);

        jdbcTemplate.update("""
                    DELETE FROM purchase_order_projections
                    WHERE purchase_order_id = :purchase_order_id
                    LIMIT 1;
                """.trim(), paramMap);
    }
}
