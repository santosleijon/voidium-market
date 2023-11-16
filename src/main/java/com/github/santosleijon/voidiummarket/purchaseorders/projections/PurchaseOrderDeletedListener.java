package com.github.santosleijon.voidiummarket.purchaseorders.projections;

import com.github.santosleijon.voidiummarket.common.eventstreaming.AggregateEventListener;
import com.github.santosleijon.voidiummarket.common.eventstreaming.EventListener;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;
import com.github.santosleijon.voidiummarket.purchaseorders.events.PurchaseOrderDeleted;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AggregateEventListener(aggregate = PurchaseOrder.aggregateName, groupId = "purchaseOrders.projections.PurchaseOrderDeleted")
public class PurchaseOrderDeletedListener extends EventListener<PurchaseOrderDeleted> {

    private final PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO;

    @Autowired
    public PurchaseOrderDeletedListener(PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO) {
        super(PurchaseOrderDeleted.class);
        this.purchaseOrderProjectionsDAO = purchaseOrderProjectionsDAO;
    }

    @Override
    public void handle(PurchaseOrderDeleted event) {
        purchaseOrderProjectionsDAO.delete(event.getAggregateId());
    }
}
