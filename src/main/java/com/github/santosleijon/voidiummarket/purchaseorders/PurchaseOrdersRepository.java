package com.github.santosleijon.voidiummarket.purchaseorders;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEventAlreadyPublished;
import com.github.santosleijon.voidiummarket.common.eventstore.EventStore;
import com.github.santosleijon.voidiummarket.purchaseorders.errors.PurchaseOrderNotSavedException;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class PurchaseOrdersRepository {

    private final EventStore eventStore;

    @Autowired
    public PurchaseOrdersRepository(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Nullable
    public PurchaseOrder get(UUID id) {
        var events = eventStore.getEventsByAggregateId(id);

        if (events.size() < 1) {
            return null;
        }

        return new PurchaseOrder(id, events);
    }

    public List<PurchaseOrder> getAll() {
        var eventsByPurchaseOrderId = eventStore.getEventsByAggregateName(PurchaseOrder.aggregateName);

        var purchaseOrders = new ArrayList<PurchaseOrder>();

        for (var purchaseOrderEntry : eventsByPurchaseOrderId.entrySet()) {
            var purchaseOrder = new PurchaseOrder(purchaseOrderEntry.getKey(), purchaseOrderEntry.getValue());
            purchaseOrders.add(purchaseOrder);
        }

        return purchaseOrders;
    }

    public void save(PurchaseOrder purchaseOrder) throws PurchaseOrderNotSavedException {
        for (var event : purchaseOrder.getPendingEvents()) {
            try {
                eventStore.publish(event);
            } catch (DomainEventAlreadyPublished e) {
                throw new PurchaseOrderNotSavedException(purchaseOrder.id, e);
            }
        }
    }
}
