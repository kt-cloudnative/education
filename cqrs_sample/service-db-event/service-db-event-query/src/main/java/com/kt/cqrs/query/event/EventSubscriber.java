package com.kt.cqrs.query.event;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.cqrs.query.repository.WithdrawalRepository;
import com.kt.cqrs.query.repository.entity.Withdrawal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
class EventSubscriber {

	private final WithdrawalRepository withdrawalRepository;

	@KafkaListener(topics="domain-event")
	public void handle(String kafkaMessage, Acknowledgment acknowledgment) {
		
		ObjectMapper mapper = new ObjectMapper();
		CardWithdrawn cardWithdrawn = null;
		try {
			cardWithdrawn  = mapper.readValue(kafkaMessage, CardWithdrawn.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
        withdrawalRepository.save(Withdrawal.newWithdrawal(UUID.randomUUID(), cardWithdrawn.getAmount(), cardWithdrawn.getCardNo()));
	}
}


