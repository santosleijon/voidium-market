package com.github.santosleijon.voidiummarket.purchaseorders;

import com.github.santosleijon.voidiummarket.purchaseorders.errors.PurchaseOrderNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @Autowired
    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping("/purchase-orders")
    public List<PurchaseOrderDTO> getAll() {
        return purchaseOrderService.getAll().stream()
                .map(PurchaseOrderDTO::new)
                .toList();
    }

    @GetMapping("/purchase-orders/{id}")
    public PurchaseOrderDTO get(@PathVariable UUID id) {
        var purchaseOrder = purchaseOrderService.get(id);

        if (purchaseOrder == null) {
            throw new PurchaseOrderNotFound();
        }

        return purchaseOrderService.get(id).toDTO();
    }

    @PostMapping("/purchase-orders")
    public void place(@RequestBody PurchaseOrder purchaseOrder) {
        purchaseOrderService.place(purchaseOrder);
    }

    @DeleteMapping("/purchase-orders/{id}")
    public void delete(@PathVariable UUID id) {
        purchaseOrderService.delete(id);
    }
}
