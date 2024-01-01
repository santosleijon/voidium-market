package com.github.santosleijon.voidiummarket.transactions;

import com.github.santosleijon.voidiummarket.common.TimeUtils;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Component
public class TransactionService {

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

    public List<PriceDetailsPerTimeUnit> getPriceDetailsPerMinute() {
        var transactions = transactionRepository.getAll();

        var transactionsGroupByMinute = transactions.stream()
                .collect(groupingBy(o -> o.getDate().truncatedTo(ChronoUnit.MINUTES)));

        return transactionsGroupByMinute.entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, TransactionService::getPriceDetailsFromTransactionsEntrySet))
                .values()
                .stream()
                .toList();
    }

    private static PriceDetailsPerTimeUnit getPriceDetailsFromTransactionsEntrySet(Map.Entry<Instant, List<Transaction>> entrySet) {
        var transactions = entrySet.getValue();
        var pricesDuringMinute = transactions.stream().map(Transaction::getPricePerUnit).toList();

        var openPrice = pricesDuringMinute.get(pricesDuringMinute.size()-1);
        var closePrice = pricesDuringMinute.get(0);
        var highPrice = Collections.max(pricesDuringMinute);
        var lowPrice = Collections.min(pricesDuringMinute);

        return new PriceDetailsPerTimeUnit(TimeUtils.getFormattedDate(entrySet.getKey()), openPrice, closePrice, highPrice, lowPrice);
    }
}
