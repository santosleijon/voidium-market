package com.github.santosleijon.voidiummarket.purchaseorders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class PurchaseOrdersController {

    private final PurchaseOrdersService purchaseOrdersService;

    @Autowired
    public PurchaseOrdersController(PurchaseOrdersService purchaseOrdersService) {
        this.purchaseOrdersService = purchaseOrdersService;
    }

    @GetMapping("/purchase-orders")
    public List<PurchaseOrder> getAll() {
        return purchaseOrdersService.getAll();
    }

    @GetMapping("/purchase-orders/{id}")
    public PurchaseOrder get(UUID id) {
        return purchaseOrdersService.get(id);
    }
}
