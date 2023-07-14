package com.github.santosleijon.voidiummarket.purchaseorders;

import com.github.santosleijon.voidiummarket.common.eventstore.EventStore;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PurchaseOrdersRepository {

    private final EventStore eventStore;

    @Autowired
    public PurchaseOrdersRepository(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Nullable
    public PurchaseOrder get(UUID id) {
        var events = eventStore.getEventsByAggregateIdAndName(id, PurchaseOrder.aggregateName);

        if (events.size() < 1) {
            return null;
        }

        var purchaseOrder = new PurchaseOrder(id, events);

        if (purchaseOrder.isDeleted()) {
            return null;
        }

        return purchaseOrder;
    }

    public boolean exists(UUID id) {
        var events = eventStore.getEventsByAggregateIdAndName(id, PurchaseOrder.aggregateName);

        return events.size() > 0;
    }

    public List<PurchaseOrder> getAll() {
        var eventsByPurchaseOrderId = eventStore.getEventsByAggregateName(PurchaseOrder.aggregateName);

        return eventsByPurchaseOrderId.entrySet().stream()
                .map(purchaseOrderEntry -> new PurchaseOrder(purchaseOrderEntry.getKey(), purchaseOrderEntry.getValue()))
                .collect(Collectors.toList());
    }

    public void save(PurchaseOrder purchaseOrder) {
        for (var event : purchaseOrder.getPendingEvents()) {
            eventStore.publish(event);
        }
    }
}
