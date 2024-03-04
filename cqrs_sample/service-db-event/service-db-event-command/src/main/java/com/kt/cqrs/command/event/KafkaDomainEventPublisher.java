package com.kt.cqrs.command.event;

import java.util.List;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaDomainEventPublisher implements DomainEventPublisher {

    private final DomainEventsStorage domainEventStorage;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void publish(DomainEvent domainEvent) {
        try {
            domainEventStorage.save(StoredDomainEvent.newStoredDomainEvent(objectMapper.writeValueAsString(domainEvent), domainEvent.getType()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Scheduled(fixedRate = 2000)
    @Transactional
    public void publishExternally() {
    	log.info("publishExternally");
    	List<StoredDomainEvent> storedDomainEvents = domainEventStorage.findAllBySentOrderByEventTimestampDesc(false);
    	for(StoredDomainEvent storedDomainEvent: storedDomainEvents) {
    		log.info("storedDomainEvent = {}",storedDomainEvent);
    		kafkaTemplate.send("domain-event", storedDomainEvent.getContent());
    		storedDomainEvent.setSent(true);
    		domainEventStorage.save(storedDomainEvent);
    	}
               
    }
 
}
