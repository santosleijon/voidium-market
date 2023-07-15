package com.github.santosleijon.voidiummarket.purchaseorders;

import com.github.santosleijon.voidiummarket.purchaseorders.errors.PurchaseOrderNotDeleted;
import com.github.santosleijon.voidiummarket.transactions.TransactionService;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PurchaseOrdersService {

    private final PurchaseOrdersRepository purchaseOrdersRepository;
    private final TransactionService transactionService;

    private static final Logger log = LoggerFactory.getLogger(PurchaseOrdersService.class);

    @Autowired
    public PurchaseOrdersService(PurchaseOrdersRepository purchaseOrdersRepository, TransactionService transactionService) {
        this.purchaseOrdersRepository = purchaseOrdersRepository;
        this.transactionService = transactionService;
    }

    public List<PurchaseOrder> getAll() {
        return purchaseOrdersRepository.getAll();
    }

    @Nullable
    public PurchaseOrder get(UUID id) {
        var purchaseOrder = purchaseOrdersRepository.get(id);

        if (purchaseOrder == null) {
            return null;
        }

        var transactions = transactionService.getForPurchaseOrder(purchaseOrder.getId());

        return purchaseOrder.setTransactions(transactions);
    }

    public void place(PurchaseOrder purchaseOrder) {
        if (purchaseOrdersRepository.exists(purchaseOrder.getId())) {
            return;
        }

        purchaseOrdersRepository.save(purchaseOrder);

        log.info("PurchaseOrder {}: Buy {} units to unit price of {} CU", purchaseOrder.getId(), purchaseOrder.getUnitsCount(), purchaseOrder.getPricePerUnit());
    }

    public void delete(UUID id) {
        var purchaseOrder = purchaseOrdersRepository.get(id);

        if (purchaseOrder == null) {
            return;
        }

        try {
            purchaseOrder.delete();
            purchaseOrdersRepository.save(purchaseOrder);
        } catch (Exception e) {
            throw new PurchaseOrderNotDeleted(id, e);
        }

        log.info("PurchaseOrder {}: Delete order of {} units to unit price of {} CU", purchaseOrder.getId(), purchaseOrder.getUnitsCount(), purchaseOrder.getPricePerUnit());
    }
}
