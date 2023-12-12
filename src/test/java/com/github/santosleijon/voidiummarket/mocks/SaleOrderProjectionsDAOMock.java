package com.github.santosleijon.voidiummarket.mocks;

import com.github.santosleijon.voidiummarket.common.FulfillmentStatus;
import com.github.santosleijon.voidiummarket.saleorders.projections.SaleOrderProjection;
import com.github.santosleijon.voidiummarket.saleorders.projections.SaleOrderProjectionsDAO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class SaleOrderProjectionsDAOMock implements SaleOrderProjectionsDAO {

    private final List<SaleOrderProjection> projections = new ArrayList<>();

    @Override
    public void upsert(SaleOrderProjection saleOrder) {
        projections.removeIf(p -> p.getId().equals(saleOrder.getId()));
        projections.add(saleOrder);
        projections.sort(Comparator.comparing(SaleOrderProjection::getPlacedDate));
    }

    @Override
    public SaleOrderProjection get(UUID saleOrderId) {
        return projections.stream().filter(p -> p.getId().equals(saleOrderId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<SaleOrderProjection> getNonDeleted() {
        return projections;
    }

    @Override
    public List<SaleOrderProjection> getUnfulfilled() {
        return projections.stream().
                filter(p -> p.getFulfillmentStatus() == FulfillmentStatus.UNFULFILLED)
                .toList();
    }

    @Override
    public void deleteAll() {
        projections.clear();
    }
}
