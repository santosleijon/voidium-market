package com.github.santosleijon.voidiummarket.saleorders;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEventAlreadyPublished;
import com.github.santosleijon.voidiummarket.common.eventstore.EventStore;
import com.github.santosleijon.voidiummarket.saleorders.errors.SaleOrderNotSaved;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class SaleOrdersRepository {

    private final EventStore eventStore;

    @Autowired
    public SaleOrdersRepository(EventStore eventStore) {
        this.eventStore = eventStore;
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
        var events = eventStore.getEventsByAggregateIdAndName(id, SaleOrder.aggregateName);

        return events.size() > 0;
    }

    public List<SaleOrder> getAll() {
        var eventsBySaleOrderId = eventStore.getEventsByAggregateName(SaleOrder.aggregateName);

        var saleOrders = new ArrayList<SaleOrder>();

        for (var saleOrderEntry : eventsBySaleOrderId.entrySet()) {
            var saleOrder = new SaleOrder(saleOrderEntry.getKey(), saleOrderEntry.getValue());

            if (!saleOrder.isDeleted()) {
                saleOrders.add(saleOrder);
            }
        }

        return saleOrders;
    }

    public void save(SaleOrder saleOrder) {
        for (var event : saleOrder.getPendingEvents()) {
            try {
                eventStore.publish(event);
            } catch (DomainEventAlreadyPublished e) {
                throw new SaleOrderNotSaved(saleOrder.getId(), e);
            }
        }
    }
}
