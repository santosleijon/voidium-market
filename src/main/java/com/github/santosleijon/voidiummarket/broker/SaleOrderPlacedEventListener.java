package com.github.santosleijon.voidiummarket.broker;

import com.github.santosleijon.voidiummarket.common.eventstreaming.AggregateEventListener;
import com.github.santosleijon.voidiummarket.common.eventstreaming.EventListener;
import com.github.santosleijon.voidiummarket.saleorders.SaleOrder;
import com.github.santosleijon.voidiummarket.saleorders.events.SaleOrderPlaced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AggregateEventListener(aggregate = SaleOrder.aggregateName, groupId = "broker.SaleOrderPlacedEventListener")
public class SaleOrderPlacedEventListener extends EventListener<SaleOrderPlaced> {

    private final BrokerService brokerService;

    @Autowired
    public SaleOrderPlacedEventListener(BrokerService brokerService) {
        super(SaleOrderPlaced.class);
        this.brokerService = brokerService;
    }

    @Override
    public void handle(SaleOrderPlaced event) {
        brokerService.brokerAvailableTransactionForSaleOrder(event.getAggregateId());
    }
}
