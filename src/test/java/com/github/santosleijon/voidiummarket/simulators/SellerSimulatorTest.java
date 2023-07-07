package com.github.santosleijon.voidiummarket.simulators;

import com.github.santosleijon.voidiummarket.saleorders.SaleOrdersService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
class SellerSimulatorTest {

    private final SimulatorConfig simulatorConfig;
    private final RandomUtil randomUtil;
    private final SaleOrdersService saleOrdersService;

    @Autowired
    public SellerSimulatorTest(SimulatorConfig simulatorConfig, RandomUtil randomUtil, SaleOrdersService saleOrdersService) {
        this.simulatorConfig = simulatorConfig;
        this.randomUtil = randomUtil;
        this.saleOrdersService = saleOrdersService;
    }

    @Test
    void runShouldCreateSaleOrder() {
        simulatorConfig.setEnabled(true);

        var sellerSimulator = new SellerSimulator(simulatorConfig, randomUtil, saleOrdersService);

        sellerSimulator.run();

        var saleOrders = saleOrdersService.getAll();

        Assertions.assertThat(saleOrders.size()).isEqualTo(1);

        var createdSaleOrder = saleOrders.get(0);

        Assertions.assertThat(createdSaleOrder.getUnitsCount()).isGreaterThanOrEqualTo(simulatorConfig.getSellerMinUnitsCount());
        Assertions.assertThat(createdSaleOrder.getUnitsCount()).isLessThanOrEqualTo(simulatorConfig.getSellerMaxUnitsCount());
        Assertions.assertThat(createdSaleOrder.getPricePerUnit()).isGreaterThanOrEqualTo(simulatorConfig.getSellerMinPricePerUnit());
        Assertions.assertThat(createdSaleOrder.getPricePerUnit()).isLessThanOrEqualTo(simulatorConfig.getSellerMaxPricePerUnit());
    }
}
