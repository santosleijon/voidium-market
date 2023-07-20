package com.github.santosleijon.voidiummarket.broker;

import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrderService;
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

    public void brokerAvailableTransactionForPurchaseOrder(UUID purchaseOrderId) {
        if (!config.isEnabled()) {
            return;
        }

        var purchaseOrder = purchaseOrderService.get(purchaseOrderId);

        if (purchaseOrder == null) {
            return;
        }

        var matchingSaleOrders = saleOrderService.getAll()
                .stream()
                .filter(saleOrder -> isMatchForTransaction(purchaseOrder, saleOrder))
                .toList();

        if (matchingSaleOrders.size() < 1) {
            return;
        }

        var matchingSaleOrder = matchingSaleOrders.get(0);

        var transaction = new Transaction(UUID.randomUUID(), purchaseOrder.getId(), matchingSaleOrder.getId(), Instant.now());

        transactionRepository.save(transaction);

        log.info("Transaction\t\t{}: Brokered transaction between purchase order {} and sale order {}", transaction.getId(), transaction.getPurchaseOrderId(), transaction.getSaleOrderId());
    }

    private static boolean isMatchForTransaction(PurchaseOrder purchaseOrder, SaleOrder saleOrder) {
        return saleOrder.getPricePerUnit().compareTo(purchaseOrder.getPricePerUnit()) <= 0 &&
                saleOrder.getUnitsCount() == purchaseOrder.getUnitsCount();
    }
}
