package com.kt.cqrs.command.event;

public interface DomainEventPublisher {

    void publish(DomainEvent event);

}