package com.github.santosleijon.voidiummarket.saleorders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.santosleijon.voidiummarket.helpers.StateClearer;
import com.github.santosleijon.voidiummarket.httpclient.HttpErrorResponse;
import com.github.santosleijon.voidiummarket.httpclient.TestHttpClient;
import com.github.santosleijon.voidiummarket.saleorders.errors.SaleOrderNotFound;
import com.github.santosleijon.voidiummarket.transactions.Transaction;
import com.github.santosleijon.voidiummarket.transactions.TransactionRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:test-application.properties")
@EmbeddedKafka(partitions = 1)
@DirtiesContext
class SaleOrderControllerTest {

    private final SaleOrderService saleOrderService;
    private final SaleOrderController saleOrderController;
    private final TransactionRepository transactionRepository;
    private final TestHttpClient testHttpClient;
    private final StateClearer stateClearer;

    @Autowired
    SaleOrderControllerTest(SaleOrderService saleOrderService, SaleOrderController saleOrderController, TransactionRepository transactionRepository, TestHttpClient testHttpClient, StateClearer stateClearer) {
        this.saleOrderService = saleOrderService;
        this.saleOrderController = saleOrderController;
        this.testHttpClient = testHttpClient;
        this.transactionRepository = transactionRepository;
        this.stateClearer = stateClearer;
    }

    @AfterEach
    void afterEach() {
        stateClearer.clear();
    }

    @Test
    void getAllShouldReturnAllSaleOrders() {
        var testSaleOrder1 = new SaleOrderBuilder().build();
        var testSaleOrder2 = new SaleOrderBuilder().build();

        saleOrderService.place(testSaleOrder1);
        saleOrderService.place(testSaleOrder2);

        Awaitility.await().untilAsserted(() -> {
            var getAllResult = testHttpClient.get("/sale-orders", new TypeReference<List<SaleOrderDTO>>() { });

            assertThat(getAllResult).contains(testSaleOrder1.toDTO(), testSaleOrder2.toDTO());
        });
    }

    @Test
    void getShouldReturnCorrectSaleOrderWithTransactionInfo() {
        var expectedSaleOrderId = UUID.randomUUID();

        var expectedTransaction = new Transaction(UUID.randomUUID(), UUID.randomUUID(), expectedSaleOrderId, 1, BigDecimal.ONE, Instant.now());

        var expectedSaleOrder = new SaleOrderBuilder().withId(expectedSaleOrderId)
                .withTransaction(expectedTransaction)
                .build();

        var irrelevantSaleOrder = new SaleOrderBuilder().build();

        saleOrderService.place(expectedSaleOrder);
        saleOrderService.place(irrelevantSaleOrder);
        transactionRepository.save(expectedTransaction);

        var actualResult = testHttpClient.get("/sale-orders/" + expectedSaleOrderId, new TypeReference<SaleOrderDTO>() { });

        assertThat(actualResult).isEqualTo(expectedSaleOrder.toDTO());
    }

    @Test
    void getShouldReturnErrorWhenSaleOrderIsNotFound() {
        var notFoundSaleOrderId = UUID.randomUUID();

        try {
            testHttpClient.get("/sale-orders/" + notFoundSaleOrderId, null);
        } catch (HttpErrorResponse httpErrorResponse) {
            assertThat(httpErrorResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }

    @Test
    void placeShouldCreateNewSaleOrder() {
        var testSaleOrder = new SaleOrderBuilder().build();

        testHttpClient.post("/sale-orders", testSaleOrder);

        var getPlacedSaleOrderResult = saleOrderController.get(testSaleOrder.getId());

        assertThat(getPlacedSaleOrderResult).isEqualTo(testSaleOrder.setTransactions(Collections.emptyList()).toDTO());
    }

    @Test
    void deleteShouldMakeSaleOrderNotRetrievable() {
        var testSaleOrder = new SaleOrderBuilder().build();

        saleOrderService.place(testSaleOrder);

        var deleteSaleOrderId = testSaleOrder.getId();

        saleOrderController.delete(deleteSaleOrderId);

        assertThatThrownBy(() -> saleOrderController.get(deleteSaleOrderId))
                .isInstanceOf(SaleOrderNotFound.class);

        assertThat(saleOrderController.getAll()).doesNotContain(testSaleOrder.toDTO());
    }
}
