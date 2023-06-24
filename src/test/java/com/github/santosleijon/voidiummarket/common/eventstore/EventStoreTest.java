package com.github.santosleijon.voidiummarket.common.eventstore;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;

class EventStoreTest {

    @Test
    void getEventsReturnsEmptyListByDefault() {
        var eventStore = new EventStore();

        Assertions.assertThat(eventStore.getEvents()).isEqualTo(emptyList());
    }

    @Test
    void getEventsReturnsEvents() throws DomainEventAlreadyPublished {
        var eventStore = new EventStore();

        var aggregateName = "aggregateName";
        var aggregateId = UUID.randomUUID();

        var event1 = new DomainEvent(UUID.randomUUID(), Instant.now(), aggregateName, aggregateId);
        eventStore.publish(event1);
        var event2 = new DomainEvent(UUID.randomUUID(), Instant.now(), aggregateName, aggregateId);
        eventStore.publish(event2);

        Assertions.assertThat(eventStore.getEvents()).isEqualTo(List.of(event1, event2));
    }

    @Test
    void getEventsForAggregateReturnsRelevantEventsOnly() throws DomainEventAlreadyPublished {
        var eventStore = new EventStore();

        var aggregateName = "aggregateName";

        var firstAggregateId = UUID.randomUUID();
        var firstAggregateEvent1 = new DomainEvent(UUID.randomUUID(), Instant.now(), aggregateName, firstAggregateId);
        eventStore.publish(firstAggregateEvent1);
        var firstAggregateEvent2 = new DomainEvent(UUID.randomUUID(), Instant.now(), aggregateName, firstAggregateId);
        eventStore.publish(firstAggregateEvent2);

        var secondAggregateId = UUID.randomUUID();
        var secondAggregateEvent = new DomainEvent(UUID.randomUUID(), Instant.now(), aggregateName, secondAggregateId);
        eventStore.publish(secondAggregateEvent);

        var expectedList = List.of(firstAggregateEvent1, firstAggregateEvent2);
        Assertions.assertThat(eventStore.getEventsByAggregateId(firstAggregateId)).isEqualTo(expectedList);
    }

    @Test
    void publishAppendsEventToEventStore() throws DomainEventAlreadyPublished {
        var eventStore = new EventStore();
        var event = new DomainEvent(UUID.randomUUID(), Instant.now(), "aggregateName", UUID.randomUUID());

        eventStore.publish(event);
        Assertions.assertThat(eventStore.getEvents()).isEqualTo(List.of(event));
    }

    @Test
    void publishThrowsExceptionWhenSameEventIsPublishedTwice() throws DomainEventAlreadyPublished {
        var eventStore = new EventStore();
        var event = new DomainEvent(UUID.randomUUID(), Instant.now(), "aggregateName", UUID.randomUUID());
        eventStore.publish(event);

        Assertions.assertThatThrownBy(() -> eventStore.publish(event)).isInstanceOf(DomainEventAlreadyPublished.class);
    }
}
