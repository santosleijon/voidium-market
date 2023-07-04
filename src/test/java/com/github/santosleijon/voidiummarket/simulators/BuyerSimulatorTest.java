package com.github.santosleijon.voidiummarket.simulators;

import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrdersService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
class BuyerSimulatorTest {

    private final SimulatorConfig simulatorConfig;
    private final RandomUtil randomUtil;
    private final PurchaseOrdersService purchaseOrdersService;

    @Autowired
    public BuyerSimulatorTest(SimulatorConfig simulatorConfig, RandomUtil randomUtil, PurchaseOrdersService purchaseOrdersService) {
        this.simulatorConfig = simulatorConfig;
        this.randomUtil = randomUtil;
        this.purchaseOrdersService = purchaseOrdersService;
    }

    @Test
    void runShouldCreatePurchaseOrder() {
        simulatorConfig.setEnabled(true);

        var buyerSimulator = new BuyerSimulator(simulatorConfig, randomUtil, purchaseOrdersService);

        buyerSimulator.run();

        var purchaseOrders = purchaseOrdersService.getAll();

        Assertions.assertThat(purchaseOrders.size()).isEqualTo(1);

        var createdPurchaseOrder = purchaseOrders.get(0);

        Assertions.assertThat(createdPurchaseOrder.getUnitsCount()).isGreaterThanOrEqualTo(simulatorConfig.getBuyerMinUnitsCount());
        Assertions.assertThat(createdPurchaseOrder.getUnitsCount()).isLessThanOrEqualTo(simulatorConfig.getBuyerMaxUnitsCount());
        Assertions.assertThat(createdPurchaseOrder.getPricePerUnit()).isGreaterThanOrEqualTo(simulatorConfig.getBuyerMinPricePerUnit());
        Assertions.assertThat(createdPurchaseOrder.getPricePerUnit()).isLessThanOrEqualTo(simulatorConfig.getBuyerMaxPricePerUnit());
    }
}
