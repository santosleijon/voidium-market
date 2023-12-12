package com.github.santosleijon.voidiummarket.saleorders.projections;

import java.util.UUID;

public class SaleOrderProjectionNotFound extends Exception {
    public SaleOrderProjectionNotFound(UUID saleOrderId) {
        super("Sale order projection for sale order ID " + saleOrderId + " does not exist");
    }
}
