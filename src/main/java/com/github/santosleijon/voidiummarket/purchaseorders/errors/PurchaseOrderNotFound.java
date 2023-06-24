package com.github.santosleijon.voidiummarket.purchaseorders.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "purchase order not found")
public class PurchaseOrderNotFound extends RuntimeException {
}
