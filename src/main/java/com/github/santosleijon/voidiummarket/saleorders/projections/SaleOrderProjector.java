package com.github.santosleijon.voidiummarket.saleorders.projections;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrder;
import com.github.santosleijon.voidiummarket.saleorders.events.SaleOrderDeleted;
import com.github.santosleijon.voidiummarket.saleorders.events.SaleOrderPlaced;
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
public class SaleOrderProjector {

    private final SaleOrderProjectionsDAO saleOrderProjectionsDAO;

    private final Logger log = LoggerFactory.getLogger(SaleOrderProjector.class);

    @Autowired
    public SaleOrderProjector(SaleOrderProjectionsDAO saleOrderProjectionsDAO) {
        this.saleOrderProjectionsDAO = saleOrderProjectionsDAO;
    }

    @RetryableTopic(
            backoff = @Backoff(value = 3000L),
            attempts = "3",
            include = SaleOrderProjectionNotFound.class
    )
    @KafkaListener(
            topics = SaleOrder.aggregateName,
            groupId = "SaleOrderProjector.handleSaleOrderEvents"
    )
    public void handleSaleOrderEvents(DomainEvent event) throws SaleOrderProjectionNotFound {
        switch (event) {
            case SaleOrderPlaced saleOrderPlacedEvent -> handleSaleOrderPlacedEvent(saleOrderPlacedEvent);
            case SaleOrderDeleted saleOrderDeletedEvent -> handleSaleOrderDeletedEvent(saleOrderDeletedEvent);
            default -> {}
        }
    }

    @RetryableTopic(
            backoff = @Backoff(value = 3000L),
            attempts = "3",
            include = SaleOrderProjectionNotFound.class
    )
    @KafkaListener(
            topics = Transaction.aggregateName,
            groupId = "SaleOrderProjector.handleTransactionEvents"
    )
    public void handleTransactionEvents(DomainEvent event) throws SaleOrderProjectionNotFound {
        if (event instanceof TransactionCompleted transactionCompletedEvent) {
            handleTransactionCompletedEvent(transactionCompletedEvent);
        }
    }

    private void handleSaleOrderPlacedEvent(SaleOrderPlaced event) {
        var saleOrder = new SaleOrder(event.getAggregateId(), Collections.singletonList(event));
        var projection = new SaleOrderProjection(saleOrder);
        saleOrderProjectionsDAO.upsert(projection);
        log.info("SaleOrder\t{}: Projection created", event.getAggregateId());
    }

    private void handleSaleOrderDeletedEvent(SaleOrderDeleted event) throws SaleOrderProjectionNotFound {
        var saleOrderProjection = saleOrderProjectionsDAO.get(event.getAggregateId());

        if (saleOrderProjection == null) {
            throw new SaleOrderProjectionNotFound(event.getAggregateId());
        }

        var updatedProjection = new SaleOrderProjection(
                saleOrderProjection.getId(),
                saleOrderProjection.getCurrentVersion(),
                saleOrderProjection.getPlacedDate(),
                saleOrderProjection.getUnitsCount(),
                saleOrderProjection.getPricePerUnit(),
                saleOrderProjection.getValidTo(),
                saleOrderProjection.getFulfillmentStatus(),
                true,
                saleOrderProjection.getTransactions());

        saleOrderProjectionsDAO.upsert(updatedProjection);

        log.info("SaleOrder\t{}: Projection updated (deleted)", event.getAggregateId());
    }

    private void handleTransactionCompletedEvent(TransactionCompleted event) throws SaleOrderProjectionNotFound {
        var saleOrderProjection = saleOrderProjectionsDAO.get(event.getSaleOrderId());

        if (saleOrderProjection == null) {
            throw new SaleOrderProjectionNotFound(event.getSaleOrderId());
        }

        var newTransaction = new Transaction(
                event.getAggregateId(),
                event.getPurchaseOrderId(),
                event.getSaleOrderId(),
                event.getUnitsCount(),
                event.getPricePerUnit(),
                event.getDate());

        saleOrderProjection.getTransactions().add(newTransaction);

        var updatedProjection = new SaleOrderProjection(
                saleOrderProjection.getId(),
                saleOrderProjection.getCurrentVersion(),
                saleOrderProjection.getPlacedDate(),
                saleOrderProjection.getUnitsCount(),
                saleOrderProjection.getPricePerUnit(),
                saleOrderProjection.getValidTo(),
                saleOrderProjection.getFulfillmentStatus(),
                saleOrderProjection.isDeleted(),
                saleOrderProjection.getTransactions());

        saleOrderProjectionsDAO.upsert(updatedProjection);

        log.info("SaleOrder\t{}: Projection updated (new transaction)", event.getAggregateId());
    }
}
