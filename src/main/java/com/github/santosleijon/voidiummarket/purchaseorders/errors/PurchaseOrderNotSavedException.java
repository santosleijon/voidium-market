package com.github.santosleijon.voidiummarket.purchaseorders.errors;

import java.util.UUID;

public class PurchaseOrderNotSavedException extends Exception {
    public PurchaseOrderNotSavedException(UUID purchaseOrderId, Throwable cause) {
        super("Unable to save purchase order with ID " + purchaseOrderId, cause);
    }
}
