package com.github.santosleijon.voidiummarket.saleorders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.santosleijon.voidiummarket.httpclient.HttpErrorResponse;
import com.github.santosleijon.voidiummarket.httpclient.TestHttpClient;
import com.github.santosleijon.voidiummarket.saleorders.errors.SaleOrderNotFound;
import com.github.santosleijon.voidiummarket.transactions.Transaction;
import com.github.santosleijon.voidiummarket.transactions.TransactionService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:test-application.properties")
class SaleOrderControllerTest {

    private final SaleOrderService saleOrderService;
    private final SaleOrderController saleOrderController;
    private final TransactionService transactionService;
    private final TestHttpClient testHttpClient;

    @Autowired
    SaleOrderControllerTest(SaleOrderService saleOrderService, SaleOrderController saleOrderController, TransactionService transactionService, TestHttpClient testHttpClient) {
        this.saleOrderService = saleOrderService;
        this.saleOrderController = saleOrderController;
        this.testHttpClient = testHttpClient;
        this.transactionService = transactionService;
    }

    @Test
    void getAllShouldReturnAllSaleOrders() {
        var testSaleOrder1 = new SaleOrderBuilder().build();
        var testSaleOrder2 = new SaleOrderBuilder().build();

        saleOrderService.place(testSaleOrder1);
        saleOrderService.place(testSaleOrder2);

        var getAllResult = testHttpClient.get("/sale-orders", new TypeReference<List<SaleOrderDTO>>() { });

        Assertions.assertThat(getAllResult).contains(testSaleOrder1.toDTO(), testSaleOrder2.toDTO());
    }

    @Test
    void getShouldReturnCorrectSaleOrderWithTransactionInfo() {
        var expectedSaleOrderId = UUID.randomUUID();

        var expectedTransaction = new Transaction(UUID.randomUUID(), UUID.randomUUID(), expectedSaleOrderId, Instant.now());

        var expectedSaleOrder = new SaleOrderBuilder().withId(expectedSaleOrderId)
                .withTransaction(expectedTransaction)
                .build();

        var irrelevantSaleOrder = new SaleOrderBuilder().build();

        saleOrderService.place(expectedSaleOrder);
        saleOrderService.place(irrelevantSaleOrder);
        transactionService.complete(expectedTransaction);

        var actualResult = testHttpClient.get("/sale-orders/" + expectedSaleOrderId, new TypeReference<SaleOrderDTO>() { });

        Assertions.assertThat(actualResult).isEqualTo(expectedSaleOrder.toDTO());
    }

    @Test
    void getShouldReturnErrorWhenSaleOrderIsNotFound() {
        var notFoundSaleOrderId = UUID.randomUUID();

        try {
            testHttpClient.get("/sale-orders/" + notFoundSaleOrderId, null);
        } catch (HttpErrorResponse httpErrorResponse) {
            Assertions.assertThat(httpErrorResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }

    @Test
    void placeShouldCreateNewSaleOrder() {
        var testSaleOrder = new SaleOrderBuilder().build();

        testHttpClient.post("/sale-orders", testSaleOrder);

        var getPlacedSaleOrderResult = saleOrderController.get(testSaleOrder.getId());

        Assertions.assertThat(getPlacedSaleOrderResult).isEqualTo(testSaleOrder.setTransactions(Collections.emptyList()).toDTO());
    }

    @Test
    void deleteShouldMakeSaleOrderNotRetrievable() {
        var testSaleOrder = new SaleOrderBuilder().build();

        saleOrderService.place(testSaleOrder);

        var deleteSaleOrderId = testSaleOrder.getId();

        saleOrderController.delete(deleteSaleOrderId);

        Assertions.assertThatThrownBy(() -> saleOrderController.get(deleteSaleOrderId))
                .isInstanceOf(SaleOrderNotFound.class);

        Assertions.assertThat(saleOrderController.getAll()).doesNotContain(testSaleOrder.toDTO());
    }
}
