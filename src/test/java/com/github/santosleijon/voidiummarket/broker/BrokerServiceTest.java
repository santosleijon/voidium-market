package com.github.santosleijon.voidiummarket.broker;

import com.github.santosleijon.voidiummarket.admin.StateClearer;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrderBuilder;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrderRepository;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrderService;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrderBuilder;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrderRepository;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrderService;
import com.github.santosleijon.voidiummarket.transactions.TransactionService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@DirtiesContext
@EmbeddedKafka(partitions = 1)
class BrokerServiceTest {

    private final BrokerConfig brokerConfig;
    private final PurchaseOrderService purchaseOrderService;
    private final SaleOrderService saleOrderService;
    private final TransactionService transactionService;
    private final StateClearer stateClearer;
    private final BrokerService brokerService;
    private final SaleOrderRepository saleOrderRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    public BrokerServiceTest(BrokerConfig brokerConfig, PurchaseOrderService purchaseOrderService, SaleOrderService saleOrderService, TransactionService transactionService, StateClearer stateClearer, BrokerService brokerService, SaleOrderRepository saleOrderRepository, PurchaseOrderRepository purchaseOrderRepository) {
        this.brokerConfig = brokerConfig;
        this.purchaseOrderService = purchaseOrderService;
        this.saleOrderService = saleOrderService;
        this.transactionService = transactionService;
        this.stateClearer = stateClearer;
        this.brokerService = brokerService;
        this.saleOrderRepository = saleOrderRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @AfterEach
    void afterEach() {
        stateClearer.deleteAllData();
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

        Awaitility.await().untilAsserted(() -> assertThat(actualCreatedTransactions.size()).isEqualTo(0));
    }

    @Test
    void brokerShouldNotCreateTransactionForAlreadyFulfilledPurchaseOrder() {
        brokerConfig.setEnabled(true);

        var purchaseOrder = new PurchaseOrderBuilder()
                .withPricePerUnit(BigDecimal.valueOf(10.00))
                .withUnitsCount(10)
                .build();

        purchaseOrderRepository.save(purchaseOrder);

        var matchingSaleOrder = new SaleOrderBuilder()
                .withUnitsCount(purchaseOrder.getUnitsCount())
                .withPricePerUnit(purchaseOrder.getPricePerUnit())
                .build();

        saleOrderRepository.save(matchingSaleOrder);

        var unfulfilledSaleOrder = new SaleOrderBuilder()
                .withUnitsCount(purchaseOrder.getUnitsCount())
                .withPricePerUnit(purchaseOrder.getPricePerUnit())
                .build();

        saleOrderService.place(unfulfilledSaleOrder);

        Awaitility.await().untilAsserted(() -> {
            brokerService.brokerUnfulfilledTransactions();

            var transactionsForUnfulfilledSaleOrder = transactionService.getForSaleOrder(unfulfilledSaleOrder.getId());

            assertThat(transactionsForUnfulfilledSaleOrder.size()).isEqualTo(0);
        });
    }

    @Test
    void brokerShouldNotCreateTransactionForAlreadyFulfilledSaleOrder() {
        brokerConfig.setEnabled(true);

        var saleOrder = new SaleOrderBuilder()
                .withPricePerUnit(BigDecimal.valueOf(10.00))
                .withUnitsCount(10)
                .build();

        saleOrderRepository.save(saleOrder);

        var matchingPurchaseOrder = new PurchaseOrderBuilder()
                .withUnitsCount(saleOrder.getUnitsCount())
                .withPricePerUnit(saleOrder.getPricePerUnit())
                .build();

        purchaseOrderRepository.save(matchingPurchaseOrder);

        var unfulfilledPurchaseOrder = new PurchaseOrderBuilder()
                .withUnitsCount(saleOrder.getUnitsCount())
                .withPricePerUnit(saleOrder.getPricePerUnit())
                .build();

        purchaseOrderService.place(unfulfilledPurchaseOrder);

        Awaitility.await().untilAsserted(() -> {
            brokerService.brokerUnfulfilledTransactions();

            var transactionsForUnfulfilledSaleOrder = transactionService.getForPurchaseOrder(unfulfilledPurchaseOrder.getId());

            assertThat(transactionsForUnfulfilledSaleOrder.size()).isEqualTo(0);
        });
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

        Awaitility.await().untilAsserted(() -> {
            brokerService.brokerUnfulfilledTransactions();

            var actualCreatedTransactions = transactionService.getForPurchaseOrder(purchaseOrder.getId());

            assertThat(actualCreatedTransactions.size()).isEqualTo(1);

            var actualCreatedTransaction = actualCreatedTransactions.get(0);

            assertThat(actualCreatedTransaction.getPurchaseOrderId()).isEqualTo(purchaseOrder.getId());
            assertThat(actualCreatedTransaction.getSaleOrderId()).isEqualTo(saleOrder.getId());
            assertThat(actualCreatedTransaction.getPricePerUnit()).isEqualTo(saleOrder.getPricePerUnit()).isEqualTo(purchaseOrder.getPricePerUnit());
            assertThat(actualCreatedTransaction.getUnitsCount()).isEqualTo(saleOrder.getUnitsCount()).isEqualTo(purchaseOrder.getUnitsCount());
        });
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

        Awaitility.await().untilAsserted(() -> {
            brokerService.brokerUnfulfilledTransactions();

            var actualCreatedTransactions = transactionService.getForPurchaseOrder(purchaseOrder.getId());

            assertThat(actualCreatedTransactions.size()).isEqualTo(1);

            var actualCreatedTransaction = actualCreatedTransactions.get(0);

            assertThat(actualCreatedTransaction.getPurchaseOrderId()).isEqualTo(purchaseOrder.getId());
            assertThat(actualCreatedTransaction.getSaleOrderId()).isEqualTo(saleOrder.getId());
            assertThat(actualCreatedTransaction.getPricePerUnit()).isEqualTo(saleOrder.getPricePerUnit()).isEqualTo(purchaseOrder.getPricePerUnit());
            assertThat(actualCreatedTransaction.getUnitsCount()).isEqualTo(saleOrder.getUnitsCount()).isEqualTo(purchaseOrder.getUnitsCount());
        });
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

        Awaitility.await().untilAsserted(() -> {
            var actualCreatedTransactions = transactionService.getForPurchaseOrder(purchaseOrder.getId());
            assertThat(actualCreatedTransactions.size()).isEqualTo(0);
        });
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

        Awaitility.await().untilAsserted(() -> {
            var actualCreatedTransactions = transactionService.getForPurchaseOrder(expiredPurchaseOrder.getId());
            assertThat(actualCreatedTransactions.size()).isEqualTo(0);
        });
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

        Awaitility.await().untilAsserted(() -> {
            var actualCreatedTransactions = transactionService.getForSaleOrder(expiredSaleOrder.getId());
            assertThat(actualCreatedTransactions.size()).isEqualTo(0);
        });
    }
}
