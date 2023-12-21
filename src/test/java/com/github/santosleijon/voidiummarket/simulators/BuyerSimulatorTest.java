package com.github.santosleijon.voidiummarket.simulators;

import com.github.santosleijon.voidiummarket.admin.StateClearer;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrderService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@EmbeddedKafka(partitions = 1)
@DirtiesContext
class BuyerSimulatorTest {

    private final SimulatorConfig simulatorConfig = new SimulatorConfig();
    private final RandomUtil randomUtil;
    private final PurchaseOrderService purchaseOrderService;
    private final StateClearer stateClearer;

    @Autowired
    public BuyerSimulatorTest(RandomUtil randomUtil, PurchaseOrderService purchaseOrderService, StateClearer stateClearer) {
        this.randomUtil = randomUtil;
        this.purchaseOrderService = purchaseOrderService;
        this.stateClearer = stateClearer;
    }

    @AfterEach
    void afterEach() {
        stateClearer.deleteAllData();
    }

    @Test
    void runShouldCreatePurchaseOrder() {
        simulatorConfig.setEnabled(true);

        var buyerSimulator = new BuyerSimulator(simulatorConfig, randomUtil, purchaseOrderService);

        buyerSimulator.run();

        Awaitility.await().untilAsserted(() -> {
            var purchaseOrders = purchaseOrderService.getAll();

            assertThat(purchaseOrders.size()).isEqualTo(1);

            var createdPurchaseOrder = purchaseOrders.get(0);

            assertThat(createdPurchaseOrder.getUnitsCount()).isGreaterThanOrEqualTo(simulatorConfig.getBuyerMinUnitsCount());
            assertThat(createdPurchaseOrder.getUnitsCount()).isLessThanOrEqualTo(simulatorConfig.getBuyerMaxUnitsCount());
            assertThat(createdPurchaseOrder.getPricePerUnit()).isGreaterThanOrEqualTo(simulatorConfig.getBuyerMinPricePerUnit());
            assertThat(createdPurchaseOrder.getPricePerUnit()).isLessThanOrEqualTo(simulatorConfig.getBuyerMaxPricePerUnit());
        });
    }
}
