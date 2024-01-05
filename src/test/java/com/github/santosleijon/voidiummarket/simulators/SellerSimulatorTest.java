package com.github.santosleijon.voidiummarket.simulators;

import com.github.santosleijon.voidiummarket.admin.StateClearer;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrderService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@EmbeddedKafka(partitions = 1, controlledShutdown = true, kraft = false)
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
        stateClearer.deleteAllData();
    }

    @Test
    void runShouldCreateSaleOrder() {
        simulatorConfig.setEnabled(true);

        var sellerSimulator = new SellerSimulator(simulatorConfig, randomUtil, saleOrderService);

        sellerSimulator.run();

        Awaitility.await().untilAsserted(() -> {
            var saleOrders = saleOrderService.getAll();

            assertThat(saleOrders.size()).isEqualTo(1);

            var createdSaleOrder = saleOrders.get(0);

            assertThat(createdSaleOrder.getUnitsCount()).isGreaterThanOrEqualTo(simulatorConfig.getSellerMinUnitsCount());
            assertThat(createdSaleOrder.getUnitsCount()).isLessThanOrEqualTo(simulatorConfig.getSellerMaxUnitsCount());
            assertThat(createdSaleOrder.getPricePerUnit()).isGreaterThanOrEqualTo(simulatorConfig.getSellerMinPricePerUnit());
            assertThat(createdSaleOrder.getPricePerUnit()).isLessThanOrEqualTo(simulatorConfig.getSellerMaxPricePerUnit());
        });
    }
}
