package com.kt.cqrs.command.repository.entity;


import java.util.Date;
import java.util.UUID;

import com.kt.cqrs.command.event.DomainEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CardWithdrawn implements DomainEvent {

    private UUID cardNo;
    private long amount;
    private Date timestamp = new Date();

    public CardWithdrawn(UUID cardNo, long amount) {
        this.cardNo = cardNo;
        this.amount = amount;
    }

    @Override
    public String getType() {
        return "card-withdrawn";
    }
}
