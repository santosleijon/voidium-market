package com.github.santosleijon.voidiummarket.saleorders;

import com.github.santosleijon.voidiummarket.saleorders.errors.SaleOrderNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class SaleOrdersController {

    private final SaleOrdersService saleOrdersService;

    @Autowired
    public SaleOrdersController(SaleOrdersService saleOrdersService) {
        this.saleOrdersService = saleOrdersService;
    }

    @GetMapping("/sale-orders")
    public List<SaleOrderDTO> getAll() {
        return saleOrdersService.getAll().stream()
                .map(SaleOrder::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/sale-orders/{id}")
    public SaleOrderDTO get(@PathVariable UUID id) {
        var saleOrder = saleOrdersService.get(id);

        if (saleOrder == null) {
            throw new SaleOrderNotFound();
        }

        return saleOrdersService.get(id).toDTO();
    }

    @PostMapping("/sale-orders")
    public void place(@RequestBody SaleOrder saleOrder) {
        saleOrdersService.place(saleOrder);
    }

    @DeleteMapping("/sale-orders/{id}")
    public void delete(@PathVariable UUID id) {
        saleOrdersService.delete(id);
    }
}
