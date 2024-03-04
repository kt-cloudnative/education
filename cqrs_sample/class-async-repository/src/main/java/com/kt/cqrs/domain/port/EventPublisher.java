package com.kt.cqrs.domain.port;

public interface EventPublisher {
	void publishEvent(DomainEvent event);
}
