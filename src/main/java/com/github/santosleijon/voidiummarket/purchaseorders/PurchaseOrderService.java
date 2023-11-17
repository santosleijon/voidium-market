package com.github.santosleijon.voidiummarket.purchaseorders;

import com.github.santosleijon.voidiummarket.purchaseorders.errors.PurchaseOrderNotDeleted;
import com.github.santosleijon.voidiummarket.purchaseorders.projections.PurchaseOrderProjection;
import com.github.santosleijon.voidiummarket.transactions.TransactionService;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final TransactionService transactionService;

    private static final Logger log = LoggerFactory.getLogger(PurchaseOrderService.class);

    @Autowired
    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository, TransactionService transactionService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.transactionService = transactionService;
    }

    public List<PurchaseOrderProjection> getAll() {
        return purchaseOrderRepository.getAllProjections();
    }

    @Nullable
    public PurchaseOrder get(UUID id) {
        var purchaseOrder = purchaseOrderRepository.get(id);

        if (purchaseOrder == null) {
            return null;
        }

        var transactions = transactionService.getForPurchaseOrder(purchaseOrder.getId());

        return purchaseOrder.setTransactions(transactions);
    }

    @Nullable
    public PurchaseOrderProjection getProjection(UUID id) {
        return purchaseOrderRepository.getProjection(id);
    }

    public void place(PurchaseOrder purchaseOrder) {
        if (purchaseOrderRepository.exists(purchaseOrder.getId())) {
            return;
        }

        purchaseOrderRepository.save(purchaseOrder);

        log.info("PurchaseOrder\t{}: Buy {} units to unit price of {} CU", purchaseOrder.getId(), purchaseOrder.getUnitsCount(), purchaseOrder.getPricePerUnit());
    }

    public void delete(UUID id) {
        var purchaseOrder = purchaseOrderRepository.get(id);

        if (purchaseOrder == null) {
            return;
        }

        try {
            purchaseOrder.delete();
            purchaseOrderRepository.save(purchaseOrder);
        } catch (Exception e) {
            throw new PurchaseOrderNotDeleted(id, e);
        }

        log.info("PurchaseOrder\t{}: Delete order of {} units to unit price of {} CU", purchaseOrder.getId(), purchaseOrder.getUnitsCount(), purchaseOrder.getPricePerUnit());
    }
}
