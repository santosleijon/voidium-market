package com.github.santosleijon.voidiummarket.saleorders;

import com.github.santosleijon.voidiummarket.saleorders.errors.SaleOrderNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class SaleOrderController {

    private final SaleOrderService saleOrderService;

    @Autowired
    public SaleOrderController(SaleOrderService saleOrderService) {
        this.saleOrderService = saleOrderService;
    }

    @GetMapping("/sale-orders")
    public List<SaleOrderDTO> getAll() {
        return saleOrderService.getAll().stream()
                .map(SaleOrderDTO::new)
                .toList();
    }

    @GetMapping("/sale-orders/{id}")
    public SaleOrderDTO get(@PathVariable UUID id) {
        var saleOrder = saleOrderService.get(id);

        if (saleOrder == null) {
            throw new SaleOrderNotFound();
        }

        return saleOrderService.get(id).toDTO();
    }

    @PostMapping("/sale-orders")
    public void place(@RequestBody SaleOrder saleOrder) {
        saleOrderService.place(saleOrder);
    }

    @DeleteMapping("/sale-orders/{id}")
    public void delete(@PathVariable UUID id) {
        saleOrderService.delete(id);
    }
}
