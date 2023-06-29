package com.github.santosleijon.voidiummarket.purchaseorders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.santosleijon.voidiummarket.httpclient.HttpErrorResponse;
import com.github.santosleijon.voidiummarket.httpclient.TestHttpClient;
import com.github.santosleijon.voidiummarket.purchaseorders.errors.PurchaseOrderNotFound;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:test-application.properties")
class PurchaseOrdersControllerTest {

    private final PurchaseOrdersService purchaseOrdersService;
    private final PurchaseOrdersController purchaseOrdersController;
    private final TestHttpClient testHttpClient;

    @Autowired
    PurchaseOrdersControllerTest(PurchaseOrdersService purchaseOrdersService, PurchaseOrdersController purchaseOrdersController, TestHttpClient testHttpClient) {
        this.purchaseOrdersService = purchaseOrdersService;
        this.purchaseOrdersController = purchaseOrdersController;
        this.testHttpClient = testHttpClient;
    }

    @Test
    void getAllShouldReturnAllPurchaseOrders() {
        var testPurchaseOrder = createTestPurchaseOrder();

        purchaseOrdersService.place(testPurchaseOrder);

        var getPurchaseOrdersResult = testHttpClient.get("/purchase-orders", new TypeReference<List<PurchaseOrderDTO>>() { });

        Assertions.assertThat(getPurchaseOrdersResult).contains(testPurchaseOrder.toDTO());
    }

    @Test
    void getPurchaseOrderShouldReturnCorrectPurchaseOrder() {
        var testPurchaseOrder = createTestPurchaseOrder();

        purchaseOrdersService.place(testPurchaseOrder);

        var getPurchaseOrderResult = testHttpClient.get("/purchase-orders/" + testPurchaseOrder.id, new TypeReference<PurchaseOrderDTO>() { });

        Assertions.assertThat(getPurchaseOrderResult).isEqualTo(testPurchaseOrder.toDTO());
    }

    @Test
    void getPurchaseOrderShouldReturnErrorWhenPurchaseOrderIsNotFound() {
        var notFoundPurchaseOrderId = UUID.randomUUID();

        try {
            testHttpClient.get("/purchase-orders/" + notFoundPurchaseOrderId, null);
        } catch (HttpErrorResponse httpErrorResponse) {
            Assertions.assertThat(httpErrorResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }

    @Test
    void placePurchaseOrderShouldCreateNewPurchaseOrder() {
        var testPurchaseOrder = createTestPurchaseOrder();

        testHttpClient.post("/purchase-orders", testPurchaseOrder);

        var getPlacedPurchaseOrderResult = purchaseOrdersController.get(testPurchaseOrder.id);

        Assertions.assertThat(getPlacedPurchaseOrderResult).isEqualTo(testPurchaseOrder.toDTO());
    }

    @Test
    void deletePurchaseOrderShouldMakePurchaseOrderNotRetrievable() {
        var testPurchaseOrder = createTestPurchaseOrder();

        purchaseOrdersService.place(testPurchaseOrder);

        testHttpClient.delete("/purchase-orders/" + testPurchaseOrder.id);

        Assertions.assertThatThrownBy(() -> purchaseOrdersController.get(testPurchaseOrder.id))
                .isInstanceOf(PurchaseOrderNotFound.class);

        Assertions.assertThat(purchaseOrdersController.getAll()).doesNotContain(testPurchaseOrder.toDTO());
    }

    private PurchaseOrder createTestPurchaseOrder() {
        return new PurchaseOrder(
                UUID.randomUUID(),
                Instant.now(),
                1,
                BigDecimal.ONE,
                Currency.getInstance("SEK"));
    }
}
