package com.github.santosleijon.voidiummarket.simulators;

import com.github.santosleijon.voidiummarket.saleorders.SaleOrder;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrderService;
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

    private final SaleOrderService saleOrderService;

    private static final Logger log = LoggerFactory.getLogger(SellerSimulator.class);

    private int executionCount = 0;

    @Autowired
    public SellerSimulator(SimulatorConfig simulatorConfig, RandomUtil randomUtil, SaleOrderService saleOrderService) {
        this.simulatorConfig = simulatorConfig;
        this.randomUtil = randomUtil;
        this.saleOrderService = saleOrderService;
    }

    @Scheduled(fixedRate = 2000)
    public void run() {
        if (!simulatorConfig.isEnabled()) {
            return;
        }

        log.debug("SellersSimulator execution #{}: Started", executionCount);

        var newSaleOrder = createNewSaleOrder();

        saleOrderService.place(newSaleOrder);

        log.debug("SellersSimulator execution #{}: Finished", executionCount);
        executionCount++;
    }

    private SaleOrder createNewSaleOrder() {
        var unitsCount = randomUtil.getRandomInt(simulatorConfig.getSellerMinUnitsCount(), simulatorConfig.getSellerMaxUnitsCount());
        var pricePerUnit = randomUtil.getRandomBigDecimal(simulatorConfig.getSellerMinPricePerUnit(), simulatorConfig.getSellerMaxPricePerUnit());

        var id = UUID.randomUUID();
        var placedDate = Instant.now();

        var validTo = placedDate.plusSeconds(60);

        return new SaleOrder(id, placedDate, unitsCount, pricePerUnit, validTo);
    }
}
