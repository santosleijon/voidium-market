package com.github.santosleijon.voidiummarket.common.eventstreaming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    private final EventStoreDAO eventStoreDAO;

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    @Autowired
    public EventPublisher(EventStoreDAO eventStoreDAO, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.eventStoreDAO = eventStoreDAO;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 1000)
    public void publishNewEvents() {
        var newEvents = eventStoreDAO.getUnpublishedEvents();

        newEvents.forEach(event -> {
            try {
                publishEvent(event);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void publishEvent(DomainEvent event) throws JsonProcessingException {
        var topic = event.getAggregateName();
        var message = objectMapper.writeValueAsString(event);

        kafkaTemplate.send(topic, message);

        var sendResultFuture = kafkaTemplate.send(topic, message);

        sendResultFuture.whenComplete((result, ex) -> {
            if (ex == null) {
                eventStoreDAO.markEventAsPublished(event.getId());

                log.info("Successfully published " + event.getClass().getSimpleName() + " event" +
                        " with ID " + event.getId() +
                        " as message=[" + message +"]" +
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
