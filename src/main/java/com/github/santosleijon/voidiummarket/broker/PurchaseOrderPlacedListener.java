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

    private static final Logger log = LoggerFactory.getLogger(PurchaseOrderPlacedListener.class);

    public PurchaseOrderPlacedListener(@Autowired EventPublisher eventPublisher) {
        super(PurchaseOrderPlaced.type, eventPublisher);
    }

    @Override
    public void receive(DomainEvent event) {
        if (event instanceof PurchaseOrderPlaced) {
            log.info("Detected new purchase order {}", event.getAggregateId());
        }
    }
}
