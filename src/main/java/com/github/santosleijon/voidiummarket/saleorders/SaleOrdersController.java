package com.github.santosleijon.voidiummarket.saleorders;

import com.github.santosleijon.voidiummarket.saleorders.errors.SaleOrderNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class SaleOrdersController {

    private final SaleOrdersService saleOrdersService;

    @Autowired
    public SaleOrdersController(SaleOrdersService saleOrdersService) {
        this.saleOrdersService = saleOrdersService;
    }

    @GetMapping("/sale-orders")
    public List<SaleOrder> getAll() {
        return saleOrdersService.getAll();
    }

    @GetMapping("/sale-orders/{id}")
    public SaleOrder get(UUID id) {
        var saleOrder = saleOrdersService.get(id);

        if (saleOrder == null) {
            throw new SaleOrderNotFound();
        }

        return saleOrdersService.get(id);
    }

    @PostMapping("/sale-orders")
    public void place(@RequestBody SaleOrder saleOrder) {
        saleOrdersService.place(saleOrder);
    }

    @DeleteMapping("/sale-orders/{id}")
    public void delete(UUID id) {
        saleOrdersService.delete(id);
    }
}
