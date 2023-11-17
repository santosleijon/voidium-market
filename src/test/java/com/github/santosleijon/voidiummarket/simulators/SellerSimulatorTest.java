package com.github.santosleijon.voidiummarket.simulators;

import com.github.santosleijon.voidiummarket.helpers.StateClearer;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrderService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@EmbeddedKafka(partitions = 1)
@DirtiesContext
class SellerSimulatorTest {

    private final SimulatorConfig simulatorConfig = new SimulatorConfig();
    private final RandomUtil randomUtil;
    private final SaleOrderService saleOrderService;
    private final StateClearer stateClearer;

    @Autowired
    public SellerSimulatorTest(RandomUtil randomUtil, SaleOrderService saleOrderService, StateClearer stateClearer) {
        this.randomUtil = randomUtil;
        this.saleOrderService = saleOrderService;
        this.stateClearer = stateClearer;
    }

    @AfterEach
    void afterEach() {
        stateClearer.clear();
    }

    @Test
    void runShouldCreateSaleOrder() {
        simulatorConfig.setEnabled(true);

        var sellerSimulator = new SellerSimulator(simulatorConfig, randomUtil, saleOrderService);

        sellerSimulator.run();

        var saleOrders = saleOrderService.getAll();

        Assertions.assertThat(saleOrders.size()).isEqualTo(1);

        var createdSaleOrder = saleOrders.get(0);

        Assertions.assertThat(createdSaleOrder.getUnitsCount()).isGreaterThanOrEqualTo(simulatorConfig.getSellerMinUnitsCount());
        Assertions.assertThat(createdSaleOrder.getUnitsCount()).isLessThanOrEqualTo(simulatorConfig.getSellerMaxUnitsCount());
        Assertions.assertThat(createdSaleOrder.getPricePerUnit()).isGreaterThanOrEqualTo(simulatorConfig.getSellerMinPricePerUnit());
        Assertions.assertThat(createdSaleOrder.getPricePerUnit()).isLessThanOrEqualTo(simulatorConfig.getSellerMaxPricePerUnit());
    }
}
