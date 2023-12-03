package com.github.santosleijon.voidiummarket.broker;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrder;
import com.github.santosleijon.voidiummarket.saleorders.events.SaleOrderPlaced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SaleOrderPlacedEventListener {

    private final BrokerService brokerService;

    @Autowired
    public SaleOrderPlacedEventListener(BrokerService brokerService) {
        this.brokerService = brokerService;
    }

    @KafkaListener(
            topics = SaleOrder.aggregateName,
            groupId = "broker.SaleOrderPlacedEventListener.handleSaleOrderPlacedEvents"
    )
    public void handleSaleOrderPlacedEvents(DomainEvent event) {
        if (event instanceof SaleOrderPlaced) {
            brokerService.brokerUnfulfilledTransactions();
        }
    }
}
