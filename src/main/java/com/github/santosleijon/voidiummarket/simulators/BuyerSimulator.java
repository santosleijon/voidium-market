package com.github.santosleijon.voidiummarket.simulators;

import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrdersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class BuyerSimulator {

    private final SimulatorConfig simulatorConfig;

    private final RandomUtil randomUtil;

    private final PurchaseOrdersService purchaseOrdersService;

    private static final Logger log = LoggerFactory.getLogger(BuyerSimulator.class);

    private int executionCount = 0;

    @Autowired
    public BuyerSimulator(SimulatorConfig simulatorConfig, RandomUtil randomUtil, PurchaseOrdersService purchaseOrdersService) {
        this.simulatorConfig = simulatorConfig;
        this.randomUtil = randomUtil;
        this.purchaseOrdersService = purchaseOrdersService;
    }

    @Scheduled(fixedRate = 2000)
    public void run() {
        log.info("BuyerSimulator execution #{}: Started", executionCount);

        var newPurchaseOrder = createNewPurchaseOrder();

        purchaseOrdersService.place(newPurchaseOrder);

        log.info("BuyerSimulator execution #{}: Finished", executionCount);
        executionCount++;
    }

    private PurchaseOrder createNewPurchaseOrder() {
        var unitsCount = randomUtil.getRandomInt(simulatorConfig.getBuyerMinUnitsCount(), simulatorConfig.getBuyerMaxUnitsCount());
        var pricePerUnit = randomUtil.getRandomBigDecimal(simulatorConfig.getBuyerMinPricePerUnit(), simulatorConfig.getBuyerMaxPricePerUnit());

        var id = UUID.randomUUID();
        var placedDate = Instant.now();

        var validTo = placedDate.plusSeconds(60);

        return new PurchaseOrder(id, placedDate, unitsCount, pricePerUnit, validTo);
    }
}
