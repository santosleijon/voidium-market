package com.github.santosleijon.voidiummarket.broker;

import com.github.santosleijon.voidiummarket.common.eventstreaming.AggregateEventListener;
import com.github.santosleijon.voidiummarket.common.eventstreaming.EventListener;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;
import com.github.santosleijon.voidiummarket.purchaseorders.events.PurchaseOrderPlaced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AggregateEventListener(aggregate = PurchaseOrder.aggregateName, groupId = "broker.PurchaseOrderPlacedEventListener")
public class PurchaseOrderPlacedEventListener extends EventListener<PurchaseOrderPlaced> {

    private final BrokerService brokerService;

    @Autowired
    public PurchaseOrderPlacedEventListener(BrokerService brokerService) {
        super(PurchaseOrderPlaced.class);
        this.brokerService = brokerService;
    }

    @Override
    public void handle(PurchaseOrderPlaced event) {
        brokerService.brokerAvailableTransactionForPurchaseOrder(event.getAggregateId());
    }
}
