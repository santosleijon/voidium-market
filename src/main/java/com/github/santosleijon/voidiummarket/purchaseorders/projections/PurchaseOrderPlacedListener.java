package com.github.santosleijon.voidiummarket.purchaseorders.projections;

import com.github.santosleijon.voidiummarket.common.eventstreaming.AggregateEventListener;
import com.github.santosleijon.voidiummarket.common.eventstreaming.EventListener;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;
import com.github.santosleijon.voidiummarket.purchaseorders.events.PurchaseOrderPlaced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@AggregateEventListener(aggregate = PurchaseOrder.aggregateName, groupId = "purchaseOrders.projections.PurchaseOrderPlaced")
public class PurchaseOrderPlacedListener extends EventListener<PurchaseOrderPlaced> {

    private final PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO;

    @Autowired
    public PurchaseOrderPlacedListener(PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO) {
        super(PurchaseOrderPlaced.class);
        this.purchaseOrderProjectionsDAO = purchaseOrderProjectionsDAO;
    }

    @Override
    public void handle(PurchaseOrderPlaced event) {
        var purchaseOrder = new PurchaseOrder(event.getAggregateId(), Collections.singletonList(event));
        purchaseOrderProjectionsDAO.upsert(purchaseOrder);
    }
}
