package com.github.santosleijon.voidiummarket.purchaseorders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.santosleijon.voidiummarket.common.eventstore.EventStore;
import com.github.santosleijon.voidiummarket.httpclient.HttpErrorResponse;
import com.github.santosleijon.voidiummarket.httpclient.TestHttpClient;
import com.github.santosleijon.voidiummarket.purchaseorders.errors.PurchaseOrderNotFound;
import com.github.santosleijon.voidiummarket.transactions.Transaction;
import com.github.santosleijon.voidiummarket.transactions.TransactionRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:test-application.properties")
class PurchaseOrderControllerTest {

    private final PurchaseOrderService purchaseOrderService;
    private final PurchaseOrderController purchaseOrderController;
    private final TransactionRepository transactionRepository;
    private final TestHttpClient testHttpClient;
    private final EventStore eventStore;

    @Autowired
    PurchaseOrderControllerTest(PurchaseOrderService purchaseOrderService, PurchaseOrderController purchaseOrderController, TransactionRepository transactionRepository, TestHttpClient testHttpClient, EventStore eventStore) {
        this.purchaseOrderService = purchaseOrderService;
        this.purchaseOrderController = purchaseOrderController;
        this.transactionRepository = transactionRepository;
        this.testHttpClient = testHttpClient;
        this.eventStore = eventStore;
    }

    @AfterEach
    void afterEach() {
        eventStore.clear();
    }

    @Test
    void getAllShouldReturnAllPurchaseOrders() {
        var testPurchaseOrder = new PurchaseOrderBuilder().build();

        purchaseOrderService.place(testPurchaseOrder);

        var getPurchaseOrdersResult = testHttpClient.get("/purchase-orders", new TypeReference<List<PurchaseOrderDTO>>() { });

        Assertions.assertThat(getPurchaseOrdersResult).contains(testPurchaseOrder.toDTO());
    }

    @Test
    void getPurchaseOrderShouldReturnCorrectPurchaseOrderWithTransactionInfo() {
        var expectedPurchaseOrderId = UUID.randomUUID();

        var expectedTransaction = new Transaction(UUID.randomUUID(), expectedPurchaseOrderId, UUID.randomUUID(), 1, BigDecimal.ONE, Instant.now());

        var expectedPurchaseOrder = new PurchaseOrderBuilder()
                .withId(expectedPurchaseOrderId)
                .withTransaction(expectedTransaction)
                .build();

        var irrelevantPurchaseOrder = new PurchaseOrderBuilder().build();

        purchaseOrderService.place(expectedPurchaseOrder);
        purchaseOrderService.place(irrelevantPurchaseOrder);
        transactionRepository.save(expectedTransaction);

        var actualPurchaseOrder = testHttpClient.get("/purchase-orders/" + expectedPurchaseOrder.getId(), new TypeReference<PurchaseOrderDTO>() { });

        Assertions.assertThat(actualPurchaseOrder).isEqualTo(expectedPurchaseOrder.toDTO());
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
        var testPurchaseOrder = new PurchaseOrderBuilder().build();

        testHttpClient.post("/purchase-orders", testPurchaseOrder);

        var getPlacedPurchaseOrderResult = purchaseOrderController.get(testPurchaseOrder.getId());

        Assertions.assertThat(getPlacedPurchaseOrderResult).isEqualTo(testPurchaseOrder.setTransactions(Collections.emptyList()).toDTO());
    }

    @Test
    void deletePurchaseOrderShouldMakePurchaseOrderNotRetrievable() {
        var testPurchaseOrder = new PurchaseOrderBuilder().build();

        purchaseOrderService.place(testPurchaseOrder);

        testHttpClient.delete("/purchase-orders/" + testPurchaseOrder.getId());

        Assertions.assertThatThrownBy(() -> purchaseOrderController.get(testPurchaseOrder.getId()))
                .isInstanceOf(PurchaseOrderNotFound.class);

        Assertions.assertThat(purchaseOrderController.getAll()).doesNotContain(testPurchaseOrder.toDTO());
    }
}
