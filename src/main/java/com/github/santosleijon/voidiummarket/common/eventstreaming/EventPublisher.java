package com.github.santosleijon.voidiummarket.common.eventstreaming;

import com.github.santosleijon.voidiummarket.common.eventstore.DomainEvent;
import com.github.santosleijon.voidiummarket.common.eventstore.EventStoreDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    private final EventStoreDAO eventStoreDAO;

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    @Autowired
    public EventPublisher(EventStoreDAO eventStoreDAO, KafkaTemplate<String, DomainEvent> kafkaTemplate) {
        this.eventStoreDAO = eventStoreDAO;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 1000)
    public void publishNewEvents() {
        var newEvents = eventStoreDAO.getUnpublishedEvents();

        newEvents.forEach(this::publishEvent);
    }

    private void publishEvent(DomainEvent event) {
        var topic = event.getAggregateName();

        var sendResultFuture = kafkaTemplate.send(topic, event);

        sendResultFuture.whenComplete((result, ex) -> {
            if (ex == null) {
                eventStoreDAO.markEventAsPublished(event.getId());

                log.info("Successfully published " + event.getClass().getSimpleName() + " event" +
                        " with ID " + event.getId() +
                        " to topic " + topic +
                        " with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                log.error("Unable to publish " + event.getClass().getSimpleName() + " event" +
                            " with ID " + event.getId() +
                            " to topic " + topic +
                            " due to : " + ex.getMessage());
            }
        });
    }
}
