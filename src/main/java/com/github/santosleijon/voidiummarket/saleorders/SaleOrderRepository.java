package com.github.santosleijon.voidiummarket.saleorders;

import com.github.santosleijon.voidiummarket.common.eventstore.EventStore;
import com.github.santosleijon.voidiummarket.saleorders.projections.SaleOrderProjection;
import com.github.santosleijon.voidiummarket.saleorders.projections.SaleOrderProjectionsDAO;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class SaleOrderRepository {

    private final EventStore eventStore;
    private final SaleOrderProjectionsDAO saleOrderProjectionsDAO;

    @Autowired
    public SaleOrderRepository(EventStore eventStore, SaleOrderProjectionsDAO saleOrderProjectionsDAO) {
        this.eventStore = eventStore;
        this.saleOrderProjectionsDAO = saleOrderProjectionsDAO;
    }

    @Nullable
    public SaleOrder get(UUID id) {
        var events = eventStore.getEventsByAggregateIdAndName(id, SaleOrder.aggregateName);

        if (events.size() < 1) {
            return null;
        }

        var saleOrder = new SaleOrder(id, events);

        if (saleOrder.isDeleted()) {
            return null;
        }

        return saleOrder;
    }

    public boolean exists(UUID id) {
        return saleOrderProjectionsDAO.get(id) != null;
    }

    public List<SaleOrderProjection> getNonDeletedProjections() {
        return saleOrderProjectionsDAO.getNonDeleted();
    }

    public List<SaleOrderProjection> getUnfulfilledProjections() {
        return saleOrderProjectionsDAO.getUnfulfilled();
    }

    public void save(SaleOrder saleOrder) {
        for (var event : saleOrder.getPendingEvents()) {
            eventStore.append(event, saleOrder.getCurrentVersion());
        }
    }
}
