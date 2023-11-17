package com.github.santosleijon.voidiummarket.purchaseorders.projections;

import com.github.santosleijon.voidiummarket.common.eventstreaming.AggregateEventListener;
import com.github.santosleijon.voidiummarket.common.eventstreaming.EventListener;
import com.github.santosleijon.voidiummarket.transactions.Transaction;
import com.github.santosleijon.voidiummarket.transactions.events.TransactionCompleted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@AggregateEventListener(aggregate = Transaction.aggregateName, groupId = "purchaseOrders.projections.TransactionCompleted")
public class TransactionCompletedListener extends EventListener<TransactionCompleted> {

    private final PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO;

    private final Logger log = LoggerFactory.getLogger(TransactionCompletedListener.class);

    @Autowired
    public TransactionCompletedListener(PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO) {
        super(TransactionCompleted.class);
        this.purchaseOrderProjectionsDAO = purchaseOrderProjectionsDAO;
    }

    @Override
    public void handle(TransactionCompleted event) {
        var completedTransaction = new Transaction(event.getAggregateId(), Collections.singletonList(event));
        var purchaseOrderProjection = purchaseOrderProjectionsDAO.get(event.getPurchaseOrderId());

        if (purchaseOrderProjection == null) {
            log.warn("Purchase order projection {} does not exists", event.getPurchaseOrderId().toString());
            return;
        }

        var updatedTransactionsList = purchaseOrderProjection.getTransactions();
        updatedTransactionsList.add(completedTransaction);

        var updatedProjection = new PurchaseOrderProjection(
                purchaseOrderProjection.getId(),
                purchaseOrderProjection.getCurrentVersion(),
                purchaseOrderProjection.getPlacedDate(),
                purchaseOrderProjection.getUnitsCount(),
                purchaseOrderProjection.getPricePerUnit(),
                purchaseOrderProjection.getValidTo(),
                purchaseOrderProjection.getFulfillmentStatus(),
                purchaseOrderProjection.isDeleted(),
                updatedTransactionsList);

        purchaseOrderProjectionsDAO.upsert(updatedProjection);
    }
}
