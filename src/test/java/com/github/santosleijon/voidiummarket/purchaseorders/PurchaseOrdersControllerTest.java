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

    private final PurchaseOrder examplePurchaseOrder = new PurchaseOrder(
            UUID.randomUUID(),
            Instant.now(),
            1,
            BigDecimal.ONE,
            Currency.getInstance("SEK"));

    @Autowired
    PurchaseOrdersControllerTest(PurchaseOrdersService purchaseOrdersService) {
        this.purchaseOrdersService = purchaseOrdersService;
        this.purchaseOrderController = new PurchaseOrdersController(purchaseOrdersService);
    }

    // TODO: Replace controller method invocations with HTTP requests

    @Test
    void getAllShouldReturnAllPurchaseOrders() throws PurchaseOrderNotSavedException {
        purchaseOrdersService.add(examplePurchaseOrder);

        var getPurchaseOrdersResult = purchaseOrderController.getAll();

        Assertions.assertThat(getPurchaseOrdersResult).contains(examplePurchaseOrder);
    }

    @Test
    void getPurchaseOrderShouldReturnCorrectPurchaseOrder() throws PurchaseOrderNotSavedException {
        purchaseOrdersService.add(examplePurchaseOrder);

        var getPurchaseOrderResult = purchaseOrderController.get(examplePurchaseOrder.id);

        Assertions.assertThat(getPurchaseOrderResult).isEqualTo(examplePurchaseOrder);
    }
}
