package com.github.santosleijon.voidiummarket.simulators;

import com.github.santosleijon.voidiummarket.saleorders.SaleOrder;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrdersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class SellerSimulator {

    private final SimulatorConfig simulatorConfig;

    private final RandomUtil randomUtil;

    private final SaleOrdersService saleOrdersService;

    private static final Logger log = LoggerFactory.getLogger(SellerSimulator.class);

    private int executionCount = 0;

    @Autowired
    public SellerSimulator(SimulatorConfig simulatorConfig, RandomUtil randomUtil, SaleOrdersService saleOrdersService) {
        this.simulatorConfig = simulatorConfig;
        this.randomUtil = randomUtil;
        this.saleOrdersService = saleOrdersService;
    }

    @Scheduled(fixedRate = 2000)
    public void run() {
        log.info("SellersSimulator execution #{}: Started", executionCount);

        var newSaleOrder = createNewSaleOrder();

        saleOrdersService.place(newSaleOrder);

        log.info("SellersSimulator execution #{}: Finished", executionCount);
        executionCount++;
    }

    private SaleOrder createNewSaleOrder() {
        var unitsCount = randomUtil.getRandomInt(simulatorConfig.getSellerMinUnitsCount(), simulatorConfig.getSellerMaxUnitsCount());
        var pricePerUnit = randomUtil.getRandomBigDecimal(simulatorConfig.getSellerMinPricePerUnit(), simulatorConfig.getSellerMaxPricePerUnit());

        var id = UUID.randomUUID();
        var placedDate = Instant.now();

        return new SaleOrder(id, placedDate, unitsCount, pricePerUnit, simulatorConfig.getCurrency());
    }
}
