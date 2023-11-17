package com.github.santosleijon.voidiummarket.broker;

import com.github.santosleijon.voidiummarket.common.FulfillmentStatus;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrderService;
import com.github.santosleijon.voidiummarket.purchaseorders.projections.PurchaseOrderProjection;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrder;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrderService;
import com.github.santosleijon.voidiummarket.transactions.Transaction;
import com.github.santosleijon.voidiummarket.transactions.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class BrokerService {

    private static final Logger log = LoggerFactory.getLogger(BrokerService.class);

    private final BrokerConfig config;
    private final TransactionRepository transactionRepository;
    private final PurchaseOrderService purchaseOrderService;
    private final SaleOrderService saleOrderService;

    @Autowired
    public BrokerService(BrokerConfig config, TransactionRepository transactionRepository, PurchaseOrderService purchaseOrderService, SaleOrderService saleOrderService) {
        this.config = config;
        this.transactionRepository = transactionRepository;
        this.purchaseOrderService = purchaseOrderService;
        this.saleOrderService = saleOrderService;
    }

    public synchronized void brokerAvailableTransactionForPurchaseOrder(UUID purchaseOrderId) {
        if (!config.isEnabled()) {
            return;
        }

        var purchaseOrder = purchaseOrderService.getProjection(purchaseOrderId);

        if (purchaseOrder == null || purchaseOrder.getFulfillmentStatus() == FulfillmentStatus.FULFILLED || !purchaseOrder.isValid()) {
            return;
        }

        var matchingSaleOrders = saleOrderService.getAllWithTransactions()
                .stream()
                .filter(saleOrder -> isMatchForTransaction(purchaseOrder, saleOrder))
                .toList();

        if (matchingSaleOrders.size() < 1) {
            return;
        }

        var matchingSaleOrder = matchingSaleOrders.get(0);

        var transaction = createTransactionForMatchingOrders(purchaseOrder, matchingSaleOrder);

        transactionRepository.save(transaction);

        log.info("Transaction\t\t{}: Brokered transaction between purchase order {} and sale order {}", transaction.getId(), transaction.getPurchaseOrderId(), transaction.getSaleOrderId());
    }

    public synchronized void brokerAvailableTransactionForSaleOrder(UUID saleOrderId) {
        if (!config.isEnabled()) {
            return;
        }

        var saleOrder = saleOrderService.get(saleOrderId);

        if (saleOrder == null || saleOrder.getFulfillmentStatus() == FulfillmentStatus.FULFILLED || !saleOrder.isValid()) {
            return;
        }

        var matchingPurchaseOrders = purchaseOrderService.getAll()
                .stream()
                .filter(purchaseOrder -> isMatchForTransaction(purchaseOrder, saleOrder))
                .toList();

        if (matchingPurchaseOrders.size() < 1) {
            return;
        }

        var matchingPurchaseOrder = matchingPurchaseOrders.get(0);

        var transaction = createTransactionForMatchingOrders(matchingPurchaseOrder, saleOrder);

        transactionRepository.save(transaction);

        log.info("Transaction\t\t{}: Brokered transaction between purchase order {} and sale order {}", transaction.getId(), transaction.getPurchaseOrderId(), transaction.getSaleOrderId());
    }

    private boolean isMatchForTransaction(PurchaseOrderProjection purchaseOrder, SaleOrder saleOrder) {
        return purchaseOrder.isValid() &&
                saleOrder.isValid() &&
                purchaseOrder.getFulfillmentStatus() != FulfillmentStatus.FULFILLED &&
                saleOrder.getFulfillmentStatus() != FulfillmentStatus.FULFILLED &&
                purchaseOrder.getPricePerUnit().compareTo(saleOrder.getPricePerUnit()) >= 0 &&
                saleOrder.getUnitsCount() == purchaseOrder.getUnitsCount();
    }

    private Transaction createTransactionForMatchingOrders(PurchaseOrderProjection purchaseOrder, SaleOrder saleOrder) {
        var transactionId = UUID.randomUUID();
        var transactionUnitCount = Math.min(purchaseOrder.getUnitsCount(), saleOrder.getUnitsCount());
        var transactionPricePerUnit = purchaseOrder.getPricePerUnit();
        var transactionDate = Instant.now();

        return new Transaction(transactionId, purchaseOrder.getId(), saleOrder.getId(), transactionUnitCount, transactionPricePerUnit, transactionDate);
    }
}
