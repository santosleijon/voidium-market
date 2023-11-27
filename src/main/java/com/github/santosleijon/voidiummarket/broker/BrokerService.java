package com.github.santosleijon.voidiummarket.broker;

import com.github.santosleijon.voidiummarket.common.FulfillmentStatus;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;
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
import java.util.ArrayList;
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

    public synchronized void brokerUnfulfilledTransactions() {
        if (!config.isEnabled()) {
            return;
        }

        var unfulfilledPurchaseOrders = new ArrayList<>(purchaseOrderService.getUnfulfilled());
        var unfulfilledSaleOrders = new ArrayList<>(saleOrderService.getUnfulfilled());

        unfulfilledPurchaseOrders.removeIf(purchaseOrder ->
                unfulfilledSaleOrders.removeIf(saleOrder -> createAndSaveTransactionForMatchingOrders(purchaseOrder, saleOrder))
        );
    }

    private boolean createAndSaveTransactionForMatchingOrders(PurchaseOrderProjection purchaseOrder, SaleOrder saleOrder) {
        if (isMatchForTransaction(purchaseOrder, saleOrder)) {
            saveTransactionForMatchingOrders(purchaseOrder, saleOrder);
            return true;
        }

        return false;
    }

    private boolean isMatchForTransaction(PurchaseOrderProjection purchaseOrder, SaleOrder saleOrder) {
        return purchaseOrder.isValid() &&
                saleOrder.isValid() &&
                purchaseOrder.getFulfillmentStatus() != FulfillmentStatus.FULFILLED &&
                saleOrder.getFulfillmentStatus() != FulfillmentStatus.FULFILLED &&
                purchaseOrder.getPricePerUnit().compareTo(saleOrder.getPricePerUnit()) >= 0 &&
                saleOrder.getUnitsCount() == purchaseOrder.getUnitsCount();
    }

    private void saveTransactionForMatchingOrders(PurchaseOrderProjection purchaseOrderProjection, SaleOrder matchingSaleOrder) {
        var purchaseOrder = purchaseOrderService.get(purchaseOrderProjection.getId());
        var saleOrder = saleOrderService.get(matchingSaleOrder.getId());

        // Ensure that orders are not fulfilled yet
        if (purchaseOrder.getFulfillmentStatus() == FulfillmentStatus.FULFILLED || saleOrder.getFulfillmentStatus() == FulfillmentStatus.FULFILLED) {
            return;
        }

        var transaction = createTransactionForMatchingOrders(purchaseOrder, saleOrder);

        transactionRepository.save(transaction);

        log.info("Transaction\t\t{}: Brokered transaction between purchase order {} and sale order {}", transaction.getId(), transaction.getPurchaseOrderId(), transaction.getSaleOrderId());
    }

    private Transaction createTransactionForMatchingOrders(PurchaseOrder purchaseOrder, SaleOrder saleOrder) {
        var transactionId = UUID.randomUUID();
        var transactionUnitCount = Math.min(purchaseOrder.getUnitsCount(), saleOrder.getUnitsCount());
        var transactionPricePerUnit = purchaseOrder.getPricePerUnit();
        var transactionDate = Instant.now();

        return new Transaction(transactionId, purchaseOrder.getId(), saleOrder.getId(), transactionUnitCount, transactionPricePerUnit, transactionDate);
    }
}
