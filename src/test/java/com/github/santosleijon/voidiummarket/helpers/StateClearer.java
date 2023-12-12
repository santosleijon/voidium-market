package com.github.santosleijon.voidiummarket.helpers;

import com.github.santosleijon.voidiummarket.common.eventstore.EventStore;
import com.github.santosleijon.voidiummarket.purchaseorders.projections.PurchaseOrderProjectionsDAO;
import com.github.santosleijon.voidiummarket.saleorders.projections.SaleOrderProjectionsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StateClearer {

    private final EventStore eventStore;
    private final PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO;
    private final SaleOrderProjectionsDAO saleOrderProjectionsDAO;

    @Autowired
    public StateClearer(EventStore eventStore, PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO, SaleOrderProjectionsDAO saleOrderProjectionsDAO) {
        this.eventStore = eventStore;
        this.purchaseOrderProjectionsDAO = purchaseOrderProjectionsDAO;
        this.saleOrderProjectionsDAO = saleOrderProjectionsDAO;
    }

    public void clear() {
        eventStore.clear();
        purchaseOrderProjectionsDAO.deleteAll();
        saleOrderProjectionsDAO.deleteAll();
        waitForEventsToBeConsumed();
    }

    private void waitForEventsToBeConsumed() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
