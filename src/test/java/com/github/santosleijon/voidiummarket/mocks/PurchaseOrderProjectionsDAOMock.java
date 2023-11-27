package com.github.santosleijon.voidiummarket.mocks;

import com.github.santosleijon.voidiummarket.common.FulfillmentStatus;
import com.github.santosleijon.voidiummarket.purchaseorders.projections.PurchaseOrderProjection;
import com.github.santosleijon.voidiummarket.purchaseorders.projections.PurchaseOrderProjectionsDAO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PurchaseOrderProjectionsDAOMock implements PurchaseOrderProjectionsDAO {

    private final List<PurchaseOrderProjection> projections = new ArrayList<>();

    @Override
    public void upsert(PurchaseOrderProjection purchaseOrder) {
        projections.removeIf(p -> p.getId().equals(purchaseOrder.getId()));
        projections.add(purchaseOrder);
        projections.sort(Comparator.comparing(PurchaseOrderProjection::getPlacedDate));
    }

    @Override
    public PurchaseOrderProjection get(UUID purchaseOrderId) {
        return projections.stream().filter(p -> p.getId().equals(purchaseOrderId)).findFirst().orElse(null);
    }

    @Override
    public List<PurchaseOrderProjection> getNonDeleted() {
        return projections;
    }

    @Override
    public List<PurchaseOrderProjection> getUnfulfilled() {
        return projections.stream().
                filter(po -> po.getFulfillmentStatus() == FulfillmentStatus.UNFULFILLED)
                .toList();
    }

    @Override
    public void deleteAll() {
        projections.clear();
    }
}
