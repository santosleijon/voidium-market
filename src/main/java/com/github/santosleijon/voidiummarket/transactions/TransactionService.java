package com.github.santosleijon.voidiummarket.transactions;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getAll() {
        return transactionRepository.getAll();
    }

    @Nullable
    public Transaction get(UUID id) {
        return transactionRepository.get(id);
    }

    public List<Transaction> getForPurchaseOrder(UUID purchaseOrderId) {
        return transactionRepository.getForPurchaseOrder(purchaseOrderId);
    }

    public List<Transaction> getForSaleOrder(UUID saleOrderId) {
        return transactionRepository.getForSaleOrder(saleOrderId);
    }

    public void complete(Transaction transaction) {
        if (transactionRepository.exists(transaction.getId())) {
            return;
        }

        transactionRepository.save(transaction);

        log.info("Transaction {}: Completed transaction between purchase order {} and sale order {}", transaction.getId(), transaction.getPurchaseOrderId(), transaction.getSaleOrderId());
    }
}
