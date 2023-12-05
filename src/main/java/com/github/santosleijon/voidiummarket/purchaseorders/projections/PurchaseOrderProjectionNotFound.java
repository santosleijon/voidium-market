package com.github.santosleijon.voidiummarket.purchaseorders.projections;

import java.util.UUID;

public class PurchaseOrderProjectionNotFound extends Exception {
    public PurchaseOrderProjectionNotFound(UUID purchaseOrderId) {
        super("Purchase order projection for purchase order ID " + purchaseOrderId + " does not exist");
    }
}
