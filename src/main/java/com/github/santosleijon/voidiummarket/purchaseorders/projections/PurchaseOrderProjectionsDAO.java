package com.github.santosleijon.voidiummarket.purchaseorders.projections;

import java.util.List;
import java.util.UUID;

public interface PurchaseOrderProjectionsDAO {
    void upsert(PurchaseOrderProjection purchaseOrder);
    PurchaseOrderProjection get(UUID purchaseOrderId);
    List<PurchaseOrderProjection> getAll();
    void deleteAll();
}
