package com.github.santosleijon.voidiummarket.purchaseorders;

import com.github.santosleijon.voidiummarket.common.eventstore.EventStore;
import com.github.santosleijon.voidiummarket.purchaseorders.projections.PurchaseOrderProjection;
import com.github.santosleijon.voidiummarket.purchaseorders.projections.PurchaseOrderProjectionsDAO;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class PurchaseOrderRepository {

    private final EventStore eventStore;
    private final PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO;

    @Autowired
    public PurchaseOrderRepository(EventStore eventStore, PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO) {
        this.eventStore = eventStore;
        this.purchaseOrderProjectionsDAO = purchaseOrderProjectionsDAO;
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


    @Nullable
    public PurchaseOrderProjection getProjection(UUID id) {
        var purchaseOrder = purchaseOrderProjectionsDAO.get(id);

        if (purchaseOrder == null || purchaseOrder.isDeleted()) {
            return null;
        }

        return purchaseOrder;
    }

    public boolean exists(UUID id) {
        return purchaseOrderProjectionsDAO.get(id) != null;
    }

    public List<PurchaseOrderProjection> getAllProjections() {
        return purchaseOrderProjectionsDAO.getAll().stream()
                .filter(purchaseOrder -> !purchaseOrder.isDeleted())
                .toList();
    }

    public void save(PurchaseOrder purchaseOrder) {
        for (var event : purchaseOrder.getPendingEvents()) {
            eventStore.append(event, purchaseOrder.getCurrentVersion());
        }
    }
}
