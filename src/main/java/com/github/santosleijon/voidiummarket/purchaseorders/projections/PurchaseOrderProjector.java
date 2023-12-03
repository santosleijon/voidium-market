package com.github.santosleijon.voidiummarket.purchaseorders.projections;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;
import com.github.santosleijon.voidiummarket.purchaseorders.events.PurchaseOrderDeleted;
import com.github.santosleijon.voidiummarket.purchaseorders.events.PurchaseOrderPlaced;
import com.github.santosleijon.voidiummarket.transactions.Transaction;
import com.github.santosleijon.voidiummarket.transactions.events.TransactionCompleted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class PurchaseOrderProjector {

    private final PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO;

    private final Logger log = LoggerFactory.getLogger(PurchaseOrderProjector.class);

    @Autowired
    public PurchaseOrderProjector(PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO) {
        this.purchaseOrderProjectionsDAO = purchaseOrderProjectionsDAO;
    }

    @KafkaListener(
            topics = PurchaseOrder.aggregateName,
            groupId = "PurchaseOrderProjection.handlePurchaseOrderEvents"
    )
    public void handlePurchaseOrderEvents(DomainEvent event) {
        switch (event) {
            case PurchaseOrderPlaced purchaseOrderPlacedEvent -> handlePurchaseOrderPlacedEvent(purchaseOrderPlacedEvent);
            case PurchaseOrderDeleted purchaseOrderDeletedEvent -> handlePurchaseOrderDeletedEvent(purchaseOrderDeletedEvent);
            default -> {}
        }
    }

    @KafkaListener(
            topics = Transaction.aggregateName,
            groupId = "PurchaseOrderProjection.handleTransactionEvents"
    )
    public void handleTransactionEvents(DomainEvent event) {
        if (event instanceof TransactionCompleted transactionCompletedEvent) {
            handleTransactionCompletedEvent(transactionCompletedEvent);
        }
    }

    private void handlePurchaseOrderPlacedEvent(PurchaseOrderPlaced event) {
        var purchaseOrder = new PurchaseOrder(event.getAggregateId(), Collections.singletonList(event));
        var projection = new PurchaseOrderProjection(purchaseOrder);
        purchaseOrderProjectionsDAO.upsert(projection);
    }

    private void handlePurchaseOrderDeletedEvent(PurchaseOrderDeleted event) {
        var purchaseOrderProjection = purchaseOrderProjectionsDAO.get(event.getAggregateId());

        if (purchaseOrderProjection == null) {
            log.warn("Purchase order projection {} does not exist", event.getAggregateId());
            return;
        }

        var updatedProjection = new PurchaseOrderProjection(
                purchaseOrderProjection.getId(),
                purchaseOrderProjection.getCurrentVersion(),
                purchaseOrderProjection.getPlacedDate(),
                purchaseOrderProjection.getUnitsCount(),
                purchaseOrderProjection.getPricePerUnit(),
                purchaseOrderProjection.getValidTo(),
                purchaseOrderProjection.getFulfillmentStatus(),
                true,
                purchaseOrderProjection.getTransactions());

        purchaseOrderProjectionsDAO.upsert(updatedProjection);
    }

    private void handleTransactionCompletedEvent(TransactionCompleted event) {
        var purchaseOrderProjection = purchaseOrderProjectionsDAO.get(event.getAggregateId());

        if (purchaseOrderProjection == null) {
            log.warn("Purchase order projection {} does not exist", event.getAggregateId());
            return;
        }

        var updatedProjection = new PurchaseOrderProjection(
                purchaseOrderProjection.getId(),
                purchaseOrderProjection.getCurrentVersion(),
                purchaseOrderProjection.getPlacedDate(),
                purchaseOrderProjection.getUnitsCount(),
                purchaseOrderProjection.getPricePerUnit(),
                purchaseOrderProjection.getValidTo(),
                purchaseOrderProjection.getFulfillmentStatus(),
                true,
                purchaseOrderProjection.getTransactions());

        purchaseOrderProjectionsDAO.upsert(updatedProjection);
    }
}
