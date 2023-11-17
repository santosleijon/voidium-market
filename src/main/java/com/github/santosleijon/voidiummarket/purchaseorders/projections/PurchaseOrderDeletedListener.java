package com.github.santosleijon.voidiummarket.purchaseorders.projections;

import com.github.santosleijon.voidiummarket.common.eventstreaming.AggregateEventListener;
import com.github.santosleijon.voidiummarket.common.eventstreaming.EventListener;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;
import com.github.santosleijon.voidiummarket.purchaseorders.events.PurchaseOrderDeleted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AggregateEventListener(aggregate = PurchaseOrder.aggregateName, groupId = "purchaseOrders.projections.PurchaseOrderDeleted")
public class PurchaseOrderDeletedListener extends EventListener<PurchaseOrderDeleted> {

    private final PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO;

    private final Logger log = LoggerFactory.getLogger(PurchaseOrderDeletedListener.class);

    @Autowired
    public PurchaseOrderDeletedListener(PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO) {
        super(PurchaseOrderDeleted.class);
        this.purchaseOrderProjectionsDAO = purchaseOrderProjectionsDAO;
    }

    @Override
    public void handle(PurchaseOrderDeleted event) {
        var purchaseOrderProjection = purchaseOrderProjectionsDAO.get(event.getAggregateId());

        if (purchaseOrderProjection == null) {
            log.warn("Purchase order projection {} does not exist", event.getAggregateId());
            return;
        }

        var updatedProjection = new PurchaseOrderProjection(
                purchaseOrderProjection.getId(),
                purchaseOrderProjection.getCurrentVersion(),
                purchaseOrderProjection.getPlacedDate(),
                purchaseOrderProjection.getUnitsCount(),
                purchaseOrderProjection.getPricePerUnit(),
                purchaseOrderProjection.getValidTo(),
                purchaseOrderProjection.getFulfillmentStatus(),
                true,
                purchaseOrderProjection.getTransactions());

        purchaseOrderProjectionsDAO.upsert(updatedProjection);
    }
}
