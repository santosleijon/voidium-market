package com.github.santosleijon.voidiummarket.purchaseorders.projections;

import com.github.santosleijon.voidiummarket.common.eventstreaming.AggregateEventListener;
import com.github.santosleijon.voidiummarket.common.eventstreaming.EventListener;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrderRepository;
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
    private final PurchaseOrderRepository purchaseOrderRepository;

    private final Logger log = LoggerFactory.getLogger(TransactionCompletedListener.class);

    @Autowired
    public TransactionCompletedListener(PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO, PurchaseOrderRepository purchaseOrderRepository) {
        super(TransactionCompleted.class);
        this.purchaseOrderProjectionsDAO = purchaseOrderProjectionsDAO;
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Override
    public void handle(TransactionCompleted event) {
        var completedTransaction = new Transaction(event.getAggregateId(), Collections.singletonList(event));
        var purchaseOrder = purchaseOrderRepository.get(event.getPurchaseOrderId());

        if (purchaseOrder == null) {
            log.warn("Purchase order {} does not exists", event.getPurchaseOrderId().toString());
            return;
        }

        var updatedList = purchaseOrder.getTransactions();
        updatedList.add(completedTransaction);

        purchaseOrder.setTransactions(updatedList);

        purchaseOrderProjectionsDAO.upsert(purchaseOrder);
    }
}
