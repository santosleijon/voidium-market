package com.github.santosleijon.voidiummarket.purchaseorders;

import com.github.santosleijon.voidiummarket.purchaseorders.errors.PurchaseOrderNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class PurchaseOrdersController {

    private final PurchaseOrdersService purchaseOrdersService;

    @Autowired
    public PurchaseOrdersController(PurchaseOrdersService purchaseOrdersService) {
        this.purchaseOrdersService = purchaseOrdersService;
    }

    @GetMapping("/purchase-orders")
    public List<PurchaseOrderDTO> getAll() {
        return purchaseOrdersService.getAll().stream()
                .map(PurchaseOrder::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/purchase-orders/{id}")
    public PurchaseOrderDTO get(@PathVariable UUID id) {
        var purchaseOrder = purchaseOrdersService.get(id);

        if (purchaseOrder == null) {
            throw new PurchaseOrderNotFound();
        }

        return purchaseOrdersService.get(id).toDTO();
    }

    @PostMapping("/purchase-orders")
    public void place(@RequestBody PurchaseOrder purchaseOrder) {
        purchaseOrdersService.place(purchaseOrder);
    }

    @DeleteMapping("/purchase-orders/{id}")
    public void delete(@PathVariable UUID id) {
        purchaseOrdersService.delete(id);
    }
}
