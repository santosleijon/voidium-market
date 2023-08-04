package com.github.santosleijon.voidiummarket.broker;

import com.github.santosleijon.voidiummarket.common.eventstore.EventStore;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrderBuilder;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrderService;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrderBuilder;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrderService;
import com.github.santosleijon.voidiummarket.transactions.TransactionService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Instant;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
class BrokerServiceTest {

    private final BrokerConfig brokerConfig;
    private final PurchaseOrderService purchaseOrderService;
    private final SaleOrderService saleOrderService;
    private final TransactionService transactionService;
    private final EventStore eventStore;

    @Autowired
    public BrokerServiceTest(BrokerConfig brokerConfig, PurchaseOrderService purchaseOrderService, SaleOrderService saleOrderService, TransactionService transactionService, EventStore eventStore) {
        this.brokerConfig = brokerConfig;
        this.purchaseOrderService = purchaseOrderService;
        this.saleOrderService = saleOrderService;
        this.transactionService = transactionService;
        this.eventStore = eventStore;
    }

    @AfterEach
    void afterEach() {
        eventStore.clear();
    }

    @Test
    void brokerShouldNotCreateTransactionForPurchaseAndSaleOrdersWithMismatchingPrice() {
        brokerConfig.setEnabled(true);

        var purchaseOrder = new PurchaseOrderBuilder()
                .withPricePerUnit(BigDecimal.valueOf(10.00))
                .build();

        purchaseOrderService.place(purchaseOrder);

        var saleOrder = new SaleOrderBuilder()
                .withPricePerUnit(BigDecimal.valueOf(20.00))
                .build();

        saleOrderService.place(saleOrder);

        var actualCreatedTransactions = transactionService.getForPurchaseOrder(purchaseOrder.getId());

        Assertions.assertThat(actualCreatedTransactions.size()).isEqualTo(0);
    }

    @Test
    void brokerShouldNotCreateTransactionForAlreadyFulfilledPurchaseOrder() {
        brokerConfig.setEnabled(true);

        var purchaseOrder = new PurchaseOrderBuilder()
                .withPricePerUnit(BigDecimal.valueOf(10.00))
                .withUnitsCount(10)
                .build();

        purchaseOrderService.place(purchaseOrder);

        var matchingSaleOrder = new SaleOrderBuilder()
                .withUnitsCount(purchaseOrder.getUnitsCount())
                .withPricePerUnit(purchaseOrder.getPricePerUnit())
                .build();

        saleOrderService.place(matchingSaleOrder);

        var unfulfilledSaleOrder = new SaleOrderBuilder()
                .withUnitsCount(purchaseOrder.getUnitsCount())
                .withPricePerUnit(purchaseOrder.getPricePerUnit())
                .build();

        saleOrderService.place(unfulfilledSaleOrder);

        var actualCreatedTransactions = transactionService.getForPurchaseOrder(purchaseOrder.getId());

        Assertions.assertThat(actualCreatedTransactions.size()).isEqualTo(1);
        Assertions.assertThat(actualCreatedTransactions.get(0).getSaleOrderId()).isEqualTo(matchingSaleOrder.getId());

        var transactionsForUnfulfilledSaleOrder = transactionService.getForSaleOrder(unfulfilledSaleOrder.getId());

        Assertions.assertThat(transactionsForUnfulfilledSaleOrder.size()).isEqualTo(0);
    }

    @Test
    void brokerShouldNotCreateTransactionForAlreadyFulfilledSaleOrder() {
        brokerConfig.setEnabled(true);

        var saleOrder = new SaleOrderBuilder()
                .withPricePerUnit(BigDecimal.valueOf(10.00))
                .withUnitsCount(10)
                .build();

        saleOrderService.place(saleOrder);

        var matchingPurchaseOrder = new PurchaseOrderBuilder()
                .withUnitsCount(saleOrder.getUnitsCount())
                .withPricePerUnit(saleOrder.getPricePerUnit())
                .build();

        purchaseOrderService.place(matchingPurchaseOrder);

        var unfulfilledPurchaseOrder = new PurchaseOrderBuilder()
                .withUnitsCount(saleOrder.getUnitsCount())
                .withPricePerUnit(saleOrder.getPricePerUnit())
                .build();

        purchaseOrderService.place(unfulfilledPurchaseOrder);

        var actualCreatedTransactions = transactionService.getForSaleOrder(saleOrder.getId());

        Assertions.assertThat(actualCreatedTransactions.size()).isEqualTo(1);
        Assertions.assertThat(actualCreatedTransactions.get(0).getPurchaseOrderId()).isEqualTo(matchingPurchaseOrder.getId());

        var transactionsForUnfulfilledSaleOrder = transactionService.getForSaleOrder(unfulfilledPurchaseOrder.getId());

        Assertions.assertThat(transactionsForUnfulfilledSaleOrder.size()).isEqualTo(0);
    }

    @Test
    void brokerShouldCreateTransactionForPurchaseAndSaleOrdersWithMatchingPriceAndQuantityWhenSaleOrderIsCreatedFirst() {
        brokerConfig.setEnabled(true);

        var saleOrder = new SaleOrderBuilder()
                .withUnitsCount(1)
                .withPricePerUnit(BigDecimal.valueOf(10.00))
                .build();

        saleOrderService.place(saleOrder);

        var purchaseOrder = new PurchaseOrderBuilder()
                .withUnitsCount(1)
                .withPricePerUnit(BigDecimal.valueOf(10.00))
                .build();

        purchaseOrderService.place(purchaseOrder);

        var actualCreatedTransactions = transactionService.getForPurchaseOrder(purchaseOrder.getId());

        Assertions.assertThat(actualCreatedTransactions.size()).isEqualTo(1);

        var actualCreatedTransaction = actualCreatedTransactions.get(0);

        Assertions.assertThat(actualCreatedTransaction.getPurchaseOrderId()).isEqualTo(purchaseOrder.getId());
        Assertions.assertThat(actualCreatedTransaction.getSaleOrderId()).isEqualTo(saleOrder.getId());
        Assertions.assertThat(actualCreatedTransaction.getPricePerUnit()).isEqualTo(saleOrder.getPricePerUnit()).isEqualTo(purchaseOrder.getPricePerUnit());
        Assertions.assertThat(actualCreatedTransaction.getUnitsCount()).isEqualTo(saleOrder.getUnitsCount()).isEqualTo(purchaseOrder.getUnitsCount());
    }

    @Test
    void brokerShouldCreateTransactionForPurchaseAndSaleOrdersWithMatchingPriceAndQuantityWhenPurchaseOrderIsCreatedFirst() {
        brokerConfig.setEnabled(true);

        var purchaseOrder = new PurchaseOrderBuilder()
                .withUnitsCount(1)
                .withPricePerUnit(BigDecimal.valueOf(10.00))
                .build();

        purchaseOrderService.place(purchaseOrder);

        var saleOrder = new SaleOrderBuilder()
                .withUnitsCount(1)
                .withPricePerUnit(BigDecimal.valueOf(10.00))
                .build();

        saleOrderService.place(saleOrder);

        var actualCreatedTransactions = transactionService.getForPurchaseOrder(purchaseOrder.getId());

        Assertions.assertThat(actualCreatedTransactions.size()).isEqualTo(1);

        var actualCreatedTransaction = actualCreatedTransactions.get(0);

        Assertions.assertThat(actualCreatedTransaction.getPurchaseOrderId()).isEqualTo(purchaseOrder.getId());
        Assertions.assertThat(actualCreatedTransaction.getSaleOrderId()).isEqualTo(saleOrder.getId());
        Assertions.assertThat(actualCreatedTransaction.getPricePerUnit()).isEqualTo(saleOrder.getPricePerUnit()).isEqualTo(purchaseOrder.getPricePerUnit());
        Assertions.assertThat(actualCreatedTransaction.getUnitsCount()).isEqualTo(saleOrder.getUnitsCount()).isEqualTo(purchaseOrder.getUnitsCount());
    }

    @Test
    void brokerShouldNotCreateTransactionForPurchaseAndSaleOrdersWithMismatchingUnitsCount() {
        brokerConfig.setEnabled(true);

        var purchaseOrder = new PurchaseOrderBuilder()
                .withUnitsCount(2)
                .build();

        purchaseOrderService.place(purchaseOrder);

        var saleOrder = new SaleOrderBuilder()
                .withUnitsCount(1)
                .build();

        saleOrderService.place(saleOrder);

        var actualCreatedTransactions = transactionService.getForPurchaseOrder(purchaseOrder.getId());

        Assertions.assertThat(actualCreatedTransactions.size()).isEqualTo(0);
    }

    @Test
    void brokerShouldNotCreateTransactionForPurchaseOrderWithExpiredDate() {
        brokerConfig.setEnabled(true);

        var expiredPurchaseOrder = new PurchaseOrderBuilder()
                .withValidTo(Instant.now().minusSeconds(60))
                .build();

        purchaseOrderService.place(expiredPurchaseOrder);

        var validSaleOrder = new SaleOrderBuilder().build();

        saleOrderService.place(validSaleOrder);

        var actualCreatedTransactions = transactionService.getForPurchaseOrder(expiredPurchaseOrder.getId());

        Assertions.assertThat(actualCreatedTransactions.size()).isEqualTo(0);
    }

    @Test
    void brokerShouldNotCreateTransactionForSaleOrderWithExpiredDate() {
        brokerConfig.setEnabled(true);

        var expiredSaleOrder = new SaleOrderBuilder()
                .withValidTo(Instant.now().minusSeconds(60))
                .build();

        saleOrderService.place(expiredSaleOrder);

        var validPurchaseOrder = new PurchaseOrderBuilder().build();

        purchaseOrderService.place(validPurchaseOrder);

        var actualCreatedTransactions = transactionService.getForSaleOrder(expiredSaleOrder.getId());

        Assertions.assertThat(actualCreatedTransactions.size()).isEqualTo(0);
    }
}
