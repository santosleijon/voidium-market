package com.github.santosleijon.voidiummarket.saleorders.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "sale order not saved")
public class SaleOrderNotSaved extends RuntimeException {
    public SaleOrderNotSaved(UUID saleOrderId, Throwable cause) {
        super("Unable to save sale order with ID " + saleOrderId, cause);
    }
}
