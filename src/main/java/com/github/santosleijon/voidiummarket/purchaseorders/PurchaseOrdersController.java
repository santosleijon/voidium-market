package com.github.santosleijon.voidiummarket.purchaseorders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PurchaseOrdersController {

    private final PurchaseOrdersService purchaseOrdersService;

    @Autowired
    public PurchaseOrdersController(PurchaseOrdersService purchaseOrdersService) {
        this.purchaseOrdersService = purchaseOrdersService;
    }

    @GetMapping("/purchase-orders")
    List<PurchaseOrder> getAll() {
        return purchaseOrdersService.getAll();
    }
}
