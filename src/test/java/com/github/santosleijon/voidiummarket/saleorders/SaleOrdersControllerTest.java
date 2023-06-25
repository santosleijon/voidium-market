package com.github.santosleijon.voidiummarket.saleorders;

import com.github.santosleijon.voidiummarket.saleorders.errors.SaleOrderNotFound;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

@SpringBootTest
class SaleOrdersControllerTest {

    private final SaleOrdersService saleOrdersService;
    private final SaleOrdersController saleOrdersController;

    @Autowired
    SaleOrdersControllerTest(SaleOrdersService saleOrdersService) {
        this.saleOrdersService = saleOrdersService;
        this.saleOrdersController = new SaleOrdersController(saleOrdersService);
    }

    @Test
    void getAllShouldReturnAllSaleOrders() {
        var exampleSaleOrder1 = createExampleSaleOrder();
        var exampleSaleOrder2 = createExampleSaleOrder();

        saleOrdersService.place(exampleSaleOrder1);
        saleOrdersService.place(exampleSaleOrder2);

        var getAllResult = saleOrdersController.getAll();

        Assertions.assertThat(getAllResult).contains(exampleSaleOrder1, exampleSaleOrder2);
    }

    @Test
    void getShouldReturnCorrectSaleOrder() {
        var exampleSaleOrder1 = createExampleSaleOrder();
        var exampleSaleOrder2 = createExampleSaleOrder();

        saleOrdersService.place(exampleSaleOrder1);
        saleOrdersService.place(exampleSaleOrder2);

        var getResult = saleOrdersController.get(exampleSaleOrder2.id);

        Assertions.assertThat(getResult).isEqualTo(exampleSaleOrder2);
    }

    @Test
    void getShouldReturnErrorWhenSaleOrderIsNotFound() {
        var notFoundSaleOrderId = UUID.randomUUID();

        Assertions.assertThatThrownBy(() -> saleOrdersController.get(notFoundSaleOrderId))
                .isInstanceOf(SaleOrderNotFound.class);
    }

    @Test
    void placeShouldCreateNewSaleOrder() {
        var exampleSaleOrder = createExampleSaleOrder();

        saleOrdersController.place(exampleSaleOrder);

        var getPlacedSaleOrderResult = saleOrdersController.get(exampleSaleOrder.id);

        Assertions.assertThat(getPlacedSaleOrderResult).isEqualTo(exampleSaleOrder);
    }

    @Test
    void deleteShouldMakeSaleOrderNotRetrievable() {
        var exampleSaleOrder = createExampleSaleOrder();

        saleOrdersService.place(exampleSaleOrder);

        var deleteSaleOrderId = exampleSaleOrder.id;

        saleOrdersController.delete(deleteSaleOrderId);

        Assertions.assertThatThrownBy(() -> saleOrdersController.get(deleteSaleOrderId))
                .isInstanceOf(SaleOrderNotFound.class);

        Assertions.assertThat(saleOrdersController.getAll()).doesNotContain(exampleSaleOrder);
    }

    private SaleOrder createExampleSaleOrder() {
        return new SaleOrder(
                UUID.randomUUID(),
                Instant.now(),
                1,
                BigDecimal.ONE,
                Currency.getInstance("SEK"));
    }
}
