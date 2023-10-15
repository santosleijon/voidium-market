package com.github.santosleijon.voidiummarket.transactions;

import com.github.santosleijon.voidiummarket.common.eventstore.EventStore;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class TransactionRepository {

    private final EventStore eventStore;

    @Autowired
    public TransactionRepository(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Nullable
    public Transaction get(UUID id) {
        var events = eventStore.getEventsByAggregateIdAndName(id, Transaction.aggregateName);

        if (events.size() < 1) {
            return null;
        }

        return new Transaction(id, events);
    }

    public List<Transaction> getAll() {
        var eventsByTransactionId = eventStore.getEventsByAggregateName(Transaction.aggregateName);

        return eventsByTransactionId.entrySet().stream()
                .map(transactionEntry -> new Transaction(transactionEntry.getKey(), transactionEntry.getValue()))
                .collect(Collectors.toList());
    }

    public List<Transaction> getForPurchaseOrder(UUID purchaseOrderId) {
        var eventsByTransactionId = eventStore.getEventsByAggregateName(Transaction.aggregateName);

        return eventsByTransactionId.entrySet().stream()
                .map(transactionEntry -> new Transaction(transactionEntry.getKey(), transactionEntry.getValue()))
                .filter(transaction -> transaction.getPurchaseOrderId().equals(purchaseOrderId))
                .collect(Collectors.toList());
    }

    public List<Transaction> getForSaleOrder(UUID saleOrderId) {
        var eventsByTransactionId = eventStore.getEventsByAggregateName(Transaction.aggregateName);

         return eventsByTransactionId.entrySet().stream()
                .map(transactionEntry -> new Transaction(transactionEntry.getKey(), transactionEntry.getValue()))
                .filter(transaction -> transaction.getSaleOrderId().equals(saleOrderId))
                .collect(Collectors.toList());
    }

    public void save(Transaction transaction) {
        for (var event : transaction.getPendingEvents()) {
            eventStore.append(event, transaction.getCurrentVersion());
        }
    }
}
