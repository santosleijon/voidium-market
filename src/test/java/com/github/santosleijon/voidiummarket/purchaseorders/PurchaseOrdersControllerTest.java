package com.github.santosleijon.voidiummarket.purchaseorders;

import com.github.santosleijon.voidiummarket.purchaseorders.errors.PurchaseOrderNotFound;
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
    private final PurchaseOrdersController purchaseOrdersController;

    private final PurchaseOrder examplePurchaseOrder = new PurchaseOrder(
            UUID.randomUUID(),
            Instant.now(),
            1,
            BigDecimal.ONE,
            Currency.getInstance("SEK"));

    @Autowired
    PurchaseOrdersControllerTest(PurchaseOrdersService purchaseOrdersService) {
        this.purchaseOrdersService = purchaseOrdersService;
        this.purchaseOrdersController = new PurchaseOrdersController(purchaseOrdersService);
    }

    // TODO: Replace controller method invocations with HTTP requests

    @Test
    void getAllShouldReturnAllPurchaseOrders() throws PurchaseOrderNotSavedException {
        purchaseOrdersService.place(examplePurchaseOrder);

        var getPurchaseOrdersResult = purchaseOrdersController.getAll();

        Assertions.assertThat(getPurchaseOrdersResult).contains(examplePurchaseOrder);
    }

    @Test
    void getPurchaseOrderShouldReturnCorrectPurchaseOrder() throws PurchaseOrderNotSavedException {
        purchaseOrdersService.place(examplePurchaseOrder);

        var getPurchaseOrderResult = purchaseOrdersController.get(examplePurchaseOrder.id);

        Assertions.assertThat(getPurchaseOrderResult).isEqualTo(examplePurchaseOrder);
    }

    @Test
    void getPurchaseOrderShouldReturnErrorWhenPurchaseOrderIsNotFound() {
        var notFoundPurchaseOrderId = UUID.randomUUID();

        Assertions.assertThatThrownBy(() -> purchaseOrdersController.get(notFoundPurchaseOrderId))
                .isInstanceOf(PurchaseOrderNotFound.class);
    }

    @Test
    void deletePurchaseOrderShouldMakePurchaseOrderNotRetrievable() {
        purchaseOrdersService.place(examplePurchaseOrder);

        var deletePurchaseOrderId = examplePurchaseOrder.id;

        purchaseOrdersController.delete(deletePurchaseOrderId);

        Assertions.assertThatThrownBy(() -> purchaseOrdersController.get(deletePurchaseOrderId))
                .isInstanceOf(PurchaseOrderNotFound.class);

        Assertions.assertThat(purchaseOrdersController.getAll()).doesNotContain(examplePurchaseOrder);
    }

    @Test
    void placePurchaseOrderShouldCreateNewPurchaseOrder() {
        purchaseOrdersController.place(examplePurchaseOrder);

        var getPlacedPurchaseOrderResult = purchaseOrdersController.get(examplePurchaseOrder.id);

        Assertions.assertThat(getPlacedPurchaseOrderResult).isEqualTo(examplePurchaseOrder);
    }
}
