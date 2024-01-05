package com.github.santosleijon.voidiummarket.admin;

import com.github.santosleijon.voidiummarket.common.eventstore.EventStore;
import com.github.santosleijon.voidiummarket.purchaseorders.projections.PurchaseOrderProjectionsDAO;
import com.github.santosleijon.voidiummarket.saleorders.projections.SaleOrderProjectionsDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StateClearer {

    private final EventStore eventStore;
    private final PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO;
    private final SaleOrderProjectionsDAO saleOrderProjectionsDAO;

    private final Logger log = LoggerFactory.getLogger(StateClearer.class);

    @Autowired
    public StateClearer(EventStore eventStore, PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO, SaleOrderProjectionsDAO saleOrderProjectionsDAO) {
        this.eventStore = eventStore;
        this.purchaseOrderProjectionsDAO = purchaseOrderProjectionsDAO;
        this.saleOrderProjectionsDAO = saleOrderProjectionsDAO;
    }

    public void deleteAllData() {
        eventStore.clear();
        purchaseOrderProjectionsDAO.deleteAll();
        saleOrderProjectionsDAO.deleteAll();

        log.info("All data was deleted");
    }
}
