package com.github.santosleijon.voidiummarket.broker;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.purchaseorders.PurchaseOrder;
import com.github.santosleijon.voidiummarket.purchaseorders.events.PurchaseOrderPlaced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderPlacedEventListener {

    private final BrokerService brokerService;

    @Autowired
    public PurchaseOrderPlacedEventListener(BrokerService brokerService) {
        this.brokerService = brokerService;
    }

    @KafkaListener(
            topics = PurchaseOrder.aggregateName,
            groupId = "broker.PurchaseOrderPlacedEventListener.handlePurchaseOrderEvents"
    )
    public void handlePurchaseOrderEvents(DomainEvent event) {
        if (event instanceof PurchaseOrderPlaced) {
            brokerService.brokerUnfulfilledTransactions();
        }
    }
}
