package com.github.santosleijon.voidiummarket.mocks;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.common.eventstore.EventStoreDAO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class EventStoreDAOMock implements EventStoreDAO {

    private final List<DomainEvent> events = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void insert(DomainEvent event) {
        events.add(event);
    }

    @Override
    public void deleteAll() {
        events.clear();
    }
}
