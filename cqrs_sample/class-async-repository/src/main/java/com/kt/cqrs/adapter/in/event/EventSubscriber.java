package com.kt.cqrs.adapter.in.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kt.cqrs.domain.port.CardWithdrawn;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
class EventSubscriber {

    private final JdbcTemplate jdbcTemplate;

    EventSubscriber(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Async
    @EventListener
    public void addWithdrawalOnCardWithdrawn(CardWithdrawn event) {
    	log.info("subscribeEvent = {}", event);
        jdbcTemplate.update("INSERT INTO WITHDRAWAL(ID, CARD_ID, AMOUNT) VALUES (?,?,?)", UUID.randomUUID(), event.getCardNo(), event.getAmount());
    }
}
