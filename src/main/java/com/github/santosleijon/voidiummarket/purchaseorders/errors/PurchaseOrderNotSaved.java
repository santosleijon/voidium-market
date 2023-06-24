package com.github.santosleijon.voidiummarket.purchaseorders.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "purchase order not saved")
public class PurchaseOrderNotSaved extends RuntimeException {
    public PurchaseOrderNotSaved(UUID purchaseOrderId, Throwable cause) {
        super("Unable to save purchase order with ID " + purchaseOrderId, cause);
    }
}
