package com.kt.cqrs.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.cqrs.repository.CreditCardRepository;
import com.kt.cqrs.repository.WithdrawalRepository;
import com.kt.cqrs.repository.entity.CreditCard;
import com.kt.cqrs.repository.entity.Withdrawal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalService {

	private final CreditCardRepository creditCardRepository;
	private final WithdrawalRepository withdrawalRepository;

	@Transactional
	public void withdraw(UUID cardId, long amount) {
		CreditCard creditCard = creditCardRepository.findById(cardId)
				.orElseThrow(() -> new IllegalStateException("Cannot find card with id " + cardId));
		withdraw(creditCard, amount);
	}

	public void withdraw(CreditCard creditCard, long amount) {
		if (thereIsMoneyToWithdraw(creditCard, amount)) {
			creditCard.setUsedLimit(creditCard.getUsedLimit() + amount);
			creditCardRepository.save(creditCard);
			withdrawalRepository.save(Withdrawal.newWithdrawal(UUID.randomUUID(), amount, creditCard.getId()));
		} else {
			throw new NotEnoughMoneyException(creditCard.getId(), amount, availableBalance(creditCard));
		}
	}

	public long availableBalance(CreditCard creditCard) {
		return creditCard.getInitialLimit() - creditCard.getUsedLimit();
	}

	private boolean thereIsMoneyToWithdraw(CreditCard creditCard, long amount) {
		return availableBalance(creditCard) >= amount;
	}

	public List<Withdrawal> withdraw(UUID cardId) {
		return withdrawalRepository.findByCardId(cardId);
	}

}
