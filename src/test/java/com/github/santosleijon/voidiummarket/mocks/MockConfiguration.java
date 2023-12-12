package com.github.santosleijon.voidiummarket.mocks;

import com.github.santosleijon.voidiummarket.common.eventstore.EventStoreDAO;
import com.github.santosleijon.voidiummarket.purchaseorders.projections.PurchaseOrderProjectionsDAO;
import com.github.santosleijon.voidiummarket.saleorders.projections.SaleOrderProjectionsDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class MockConfiguration {

    @Profile({"test"})
    @Bean
    public EventStoreDAO eventStoreDAO() {
        return new EventStoreDAOMock();
    }

    @Profile({"test"})
    @Bean
    public PurchaseOrderProjectionsDAO purchaseOrderProjectionsDAO() {
        return new PurchaseOrderProjectionsDAOMock();
    }

    @Profile({"test"})
    @Bean
    public SaleOrderProjectionsDAO saleOrderProjectionsDAO() {
        return new SaleOrderProjectionsDAOMock();
    }
}
