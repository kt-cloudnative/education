package com.kt.cqrs.command.event;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface DomainEventsStorage extends CrudRepository<StoredDomainEvent, String> {
    List<StoredDomainEvent> findAllBySentOrderByEventTimestampDesc(boolean sent);
}