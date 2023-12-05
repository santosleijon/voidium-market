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
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
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

    @RetryableTopic(
            backoff = @Backoff(value = 3000L),
            attempts = "3",
            include = PurchaseOrderProjectionNotFound.class
    )
    @KafkaListener(
            topics = PurchaseOrder.aggregateName,
            groupId = "PurchaseOrderProjection.handlePurchaseOrderEvents"
    )
    public void handlePurchaseOrderEvents(DomainEvent event) throws PurchaseOrderProjectionNotFound {
        switch (event) {
            case PurchaseOrderPlaced purchaseOrderPlacedEvent -> handlePurchaseOrderPlacedEvent(purchaseOrderPlacedEvent);
            case PurchaseOrderDeleted purchaseOrderDeletedEvent -> handlePurchaseOrderDeletedEvent(purchaseOrderDeletedEvent);
            default -> {}
        }
    }

    @RetryableTopic(
            backoff = @Backoff(value = 3000L),
            attempts = "3",
            include = PurchaseOrderProjectionNotFound.class
    )
    @KafkaListener(
            topics = Transaction.aggregateName,
            groupId = "PurchaseOrderProjection.handleTransactionEvents"
    )
    public void handleTransactionEvents(DomainEvent event) throws PurchaseOrderProjectionNotFound {
        if (event instanceof TransactionCompleted transactionCompletedEvent) {
            handleTransactionCompletedEvent(transactionCompletedEvent);
        }
    }

    private void handlePurchaseOrderPlacedEvent(PurchaseOrderPlaced event) {
        var purchaseOrder = new PurchaseOrder(event.getAggregateId(), Collections.singletonList(event));
        var projection = new PurchaseOrderProjection(purchaseOrder);
        purchaseOrderProjectionsDAO.upsert(projection);
        log.info("PurchaseOrder\t{}: Projection created", event.getAggregateId());
    }

    private void handlePurchaseOrderDeletedEvent(PurchaseOrderDeleted event) throws PurchaseOrderProjectionNotFound {
        var purchaseOrderProjection = purchaseOrderProjectionsDAO.get(event.getAggregateId());

        if (purchaseOrderProjection == null) {
            throw new PurchaseOrderProjectionNotFound(event.getAggregateId());
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

        log.info("PurchaseOrder\t{}: Projection updated (deleted)", event.getAggregateId());
    }

    private void handleTransactionCompletedEvent(TransactionCompleted event) throws PurchaseOrderProjectionNotFound {
        var purchaseOrderProjection = purchaseOrderProjectionsDAO.get(event.getPurchaseOrderId());

        if (purchaseOrderProjection == null) {
            throw new PurchaseOrderProjectionNotFound(event.getPurchaseOrderId());
        }

        var newTransaction = new Transaction(
                event.getAggregateId(),
                event.getPurchaseOrderId(),
                event.getSaleOrderId(),
                event.getUnitsCount(),
                event.getPricePerUnit(),
                event.getDate());

        purchaseOrderProjection.getTransactions().add(newTransaction);

        var updatedProjection = new PurchaseOrderProjection(
                purchaseOrderProjection.getId(),
                purchaseOrderProjection.getCurrentVersion(),
                purchaseOrderProjection.getPlacedDate(),
                purchaseOrderProjection.getUnitsCount(),
                purchaseOrderProjection.getPricePerUnit(),
                purchaseOrderProjection.getValidTo(),
                purchaseOrderProjection.getFulfillmentStatus(),
                purchaseOrderProjection.isDeleted(),
                purchaseOrderProjection.getTransactions());

        purchaseOrderProjectionsDAO.upsert(updatedProjection);

        log.info("PurchaseOrder\t{}: Projection updated (new transaction)", event.getAggregateId());
    }
}
