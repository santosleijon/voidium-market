package com.github.santosleijon.voidiummarket.mocks;

import com.github.santosleijon.voidiummarket.common.eventstore.EventStoreDAO;
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
}
