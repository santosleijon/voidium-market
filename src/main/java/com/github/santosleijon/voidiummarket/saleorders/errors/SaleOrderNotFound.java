package com.github.santosleijon.voidiummarket.saleorders.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "sale order not found")
public class SaleOrderNotFound extends RuntimeException {
}
