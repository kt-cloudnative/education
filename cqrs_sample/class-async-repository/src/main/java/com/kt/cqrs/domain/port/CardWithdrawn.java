package com.kt.cqrs.domain.port;

import java.util.UUID;

import lombok.Value;

@Value
public class CardWithdrawn implements DomainEvent {

    private final UUID cardNo;
    private final long amount;

}
