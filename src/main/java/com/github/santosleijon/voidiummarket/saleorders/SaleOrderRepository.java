package com.github.santosleijon.voidiummarket.saleorders;

import com.github.santosleijon.voidiummarket.common.eventstore.EventStore;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class SaleOrderRepository {

    private final EventStore eventStore;

    @Autowired
    public SaleOrderRepository(EventStore eventStore) {
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

        return eventsBySaleOrderId.entrySet().stream()
                .map(saleOrderEntry -> new SaleOrder(saleOrderEntry.getKey(), saleOrderEntry.getValue()))
                .filter(saleOrder -> !saleOrder.isDeleted())
                .collect(Collectors.toList());
    }

    public void save(SaleOrder saleOrder) {
        for (var event : saleOrder.getPendingEvents()) {
            eventStore.append(event, saleOrder.getCurrentVersion());
        }
    }
}
