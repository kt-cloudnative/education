package com.kt.cqrs.query.event;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.cqrs.query.event.message.Envelope;
import com.kt.cqrs.query.repository.WithdrawalRepository;
import com.kt.cqrs.query.repository.entity.Withdrawal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
class EventHandler {

	private final WithdrawalRepository withdrawalRepository;
	
	@KafkaListener(topics="credit_card")
	public void handle(String kafkaMessage, Acknowledgment acknowledgment) {
		
		ObjectMapper mapper = new ObjectMapper();
		Envelope message = null;
		try {
			message  = mapper.readValue(kafkaMessage, Envelope.class);
			log.info("message = {}", message);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		String op = message.getPayload().getOp();
		if("u".equals(op)) {
			saveWithdrawalFrom(message);
		}
	}
	

	private void saveWithdrawalFrom(Envelope message) {
		String cardId = message.getPayload().getBefore().getId();
		long withdrawalAmount
				= balanceAfter(message) - balanceBefore(message);
		 withdrawalRepository.save(Withdrawal.newWithdrawal(UUID.randomUUID(), withdrawalAmount, UUID.fromString(cardId)));
	}

	private long balanceAfter(Envelope message) {
		return message.getPayload().getAfter().getUsedLimit();
	}

	private long balanceBefore(Envelope message) {
		return message.getPayload().getBefore().getUsedLimit();
	}


}
