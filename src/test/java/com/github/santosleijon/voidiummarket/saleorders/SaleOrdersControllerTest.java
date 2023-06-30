package com.github.santosleijon.voidiummarket.saleorders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.santosleijon.voidiummarket.httpclient.HttpErrorResponse;
import com.github.santosleijon.voidiummarket.httpclient.TestHttpClient;
import com.github.santosleijon.voidiummarket.saleorders.errors.SaleOrderNotFound;
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
class SaleOrdersControllerTest {

    private final SaleOrdersService saleOrdersService;
    private final SaleOrdersController saleOrdersController;
    private final TestHttpClient testHttpClient;

    @Autowired
    SaleOrdersControllerTest(SaleOrdersService saleOrdersService, SaleOrdersController saleOrdersController, TestHttpClient testHttpClient) {
        this.saleOrdersService = saleOrdersService;
        this.saleOrdersController = saleOrdersController;
        this.testHttpClient = testHttpClient;
    }

    @Test
    void getAllShouldReturnAllSaleOrders() {
        var testSaleOrder1 = createTestSaleOrder();
        var testSaleOrder2 = createTestSaleOrder();

        saleOrdersService.place(testSaleOrder1);
        saleOrdersService.place(testSaleOrder2);

        var getAllResult = testHttpClient.get("/sale-orders", new TypeReference<List<SaleOrderDTO>>() { });

        Assertions.assertThat(getAllResult).contains(testSaleOrder1.toDTO(), testSaleOrder2.toDTO());
    }

    @Test
    void getShouldReturnCorrectSaleOrder() {
        var testSaleOrder1 = createTestSaleOrder();
        var testSaleOrder2 = createTestSaleOrder();

        saleOrdersService.place(testSaleOrder1);
        saleOrdersService.place(testSaleOrder2);

        var getResult = testHttpClient.get("/sale-orders/" + testSaleOrder2.getId(), new TypeReference<SaleOrderDTO>() { });

        Assertions.assertThat(getResult).isEqualTo(testSaleOrder2.toDTO());
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
        var testSaleOrder = createTestSaleOrder();

        testHttpClient.post("/sale-orders", testSaleOrder);

        var getPlacedSaleOrderResult = saleOrdersController.get(testSaleOrder.getId());

        Assertions.assertThat(getPlacedSaleOrderResult).isEqualTo(testSaleOrder.toDTO());
    }

    @Test
    void deleteShouldMakeSaleOrderNotRetrievable() {
        var testSaleOrder = createTestSaleOrder();

        saleOrdersService.place(testSaleOrder);

        var deleteSaleOrderId = testSaleOrder.getId();

        saleOrdersController.delete(deleteSaleOrderId);

        Assertions.assertThatThrownBy(() -> saleOrdersController.get(deleteSaleOrderId))
                .isInstanceOf(SaleOrderNotFound.class);

        Assertions.assertThat(saleOrdersController.getAll()).doesNotContain(testSaleOrder.toDTO());
    }

    private SaleOrder createTestSaleOrder() {
        return new SaleOrder(
                UUID.randomUUID(),
                Instant.now(),
                1,
                BigDecimal.ONE,
                Currency.getInstance("SEK"));
    }
}
