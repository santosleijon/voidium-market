package com.github.santosleijon.voidiummarket.helpers;

import com.github.santosleijon.voidiummarket.common.eventstore.EventStore;
import com.github.santosleijon.voidiummarket.purchaseorders.projections.PurchaseOrderProjectionsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StateClearer {

    private final EventStore eventStore;
    private final PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO;

    @Autowired
    public StateClearer(EventStore eventStore, PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO) {
        this.eventStore = eventStore;
        this.purchaseOrderProjectionsDAO = purchaseOrderProjectionsDAO;
    }

    public void clear() {
        eventStore.clear();
        purchaseOrderProjectionsDAO.deleteAll();
    }
}
