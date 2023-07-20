package com.github.santosleijon.voidiummarket.broker;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.common.eventstore.EventPublisher;
import com.github.santosleijon.voidiummarket.common.eventstore.EventSubscriber;
import com.github.santosleijon.voidiummarket.purchaseorders.events.PurchaseOrderPlaced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderPlacedListener extends EventSubscriber {

    private final BrokerService brokerService;

    private static final Logger log = LoggerFactory.getLogger(PurchaseOrderPlacedListener.class);

    @Autowired
    public PurchaseOrderPlacedListener(EventPublisher eventPublisher, BrokerService brokerService) {
        super(PurchaseOrderPlaced.type, eventPublisher);
        this.brokerService = brokerService;
    }

    @Override
    public void receive(DomainEvent event) {
        if (event instanceof PurchaseOrderPlaced) {
            log.debug("Detected new purchase order {}", event.getAggregateId());

            brokerService.brokerAvailableTransactionForPurchaseOrder(event.getAggregateId());
        }
    }
}
