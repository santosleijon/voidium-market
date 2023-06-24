package com.github.santosleijon.voidiummarket.purchaseorders;

import com.github.santosleijon.voidiummarket.purchaseorders.errors.PurchaseOrderNotSavedException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

@SpringBootTest
class PurchaseOrdersControllerTest {

    private final PurchaseOrdersService purchaseOrdersService;
    private final PurchaseOrdersController purchaseOrderController;

    @Autowired
    PurchaseOrdersControllerTest(PurchaseOrdersService purchaseOrdersService) {
        this.purchaseOrdersService = purchaseOrdersService;
        this.purchaseOrderController = new PurchaseOrdersController(purchaseOrdersService);
    }

    @Test
    void getAllShouldReturnAllPurchaseOrders() throws PurchaseOrderNotSavedException {
        var purchaseOrder = new PurchaseOrder(
                UUID.randomUUID(),
                Instant.now(),
                1,
                BigDecimal.ONE,
                Currency.getInstance("SEK"));

        purchaseOrdersService.add(purchaseOrder);

        // TODO: Replace with HTTP request
        var getPurchaseOrdersResult = purchaseOrderController.getAll();

        Assertions.assertThat(getPurchaseOrdersResult).contains(purchaseOrder);
    }
}
