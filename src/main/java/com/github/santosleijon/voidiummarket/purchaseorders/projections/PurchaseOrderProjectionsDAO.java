package com.github.santosleijon.voidiummarket.purchaseorders.projections;

import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;
import java.util.UUID;

public interface PurchaseOrderProjectionsDAO {
    void upsert(PurchaseOrder purchaseOrder);
    void delete(UUID purchaseOrderId);
}
