package com.github.santosleijon.voidiummarket.broker;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.common.eventstore.EventPublisher;
import com.github.santosleijon.voidiummarket.common.eventstore.EventSubscriber;
import com.github.santosleijon.voidiummarket.saleorders.events.SaleOrderPlaced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SaleOrderPlacedListener extends EventSubscriber {

    private final BrokerService brokerService;

    private static final Logger log = LoggerFactory.getLogger(SaleOrderPlacedListener.class);

    @Autowired
    public SaleOrderPlacedListener(EventPublisher eventPublisher, BrokerService brokerService) {
        super(SaleOrderPlaced.type, eventPublisher);
        this.brokerService = brokerService;
    }

    @Override
    public void receive(DomainEvent event) {
        if (event instanceof SaleOrderPlaced) {
            log.debug("Detected new sale order {}", event.getAggregateId());

            brokerService.brokerAvailableTransactionForSaleOrder(event.getAggregateId());
        }
    }
}
