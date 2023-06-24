package com.github.santosleijon.voidiummarket.purchaseorders;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEventAlreadyPublished;
import com.github.santosleijon.voidiummarket.common.eventstore.EventStore;
import com.github.santosleijon.voidiummarket.purchaseorders.errors.PurchaseOrderNotSaved;
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

        var purchaseOrder = new PurchaseOrder(id, events);

        if (purchaseOrder.deleted) {
            return null;
        }

        return purchaseOrder;
    }

    public List<PurchaseOrder> getAll() {
        var eventsByPurchaseOrderId = eventStore.getEventsByAggregateName(PurchaseOrder.aggregateName);

        var purchaseOrders = new ArrayList<PurchaseOrder>();

        for (var purchaseOrderEntry : eventsByPurchaseOrderId.entrySet()) {
            var purchaseOrder = new PurchaseOrder(purchaseOrderEntry.getKey(), purchaseOrderEntry.getValue());

            if (!purchaseOrder.deleted) {
                purchaseOrders.add(purchaseOrder);
            }
        }

        return purchaseOrders;
    }

    public void save(PurchaseOrder purchaseOrder) {
        for (var event : purchaseOrder.getPendingEvents()) {
            try {
                eventStore.publish(event);
            } catch (DomainEventAlreadyPublished e) {
                throw new PurchaseOrderNotSaved(purchaseOrder.id, e);
            }
        }
    }
}
