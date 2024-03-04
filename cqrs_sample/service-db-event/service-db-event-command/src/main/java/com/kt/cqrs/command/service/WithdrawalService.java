package com.kt.cqrs.command.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.cqrs.command.event.DomainEventPublisher;
import com.kt.cqrs.command.repository.CreditCardRepository;
import com.kt.cqrs.command.repository.entity.CardWithdrawn;
import com.kt.cqrs.command.repository.entity.CreditCard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalService {

    private final CreditCardRepository creditCardRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Transactional
    public void withdraw(UUID cardId, long amount) {
        CreditCard creditCard = creditCardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalStateException("Cannot find card with id " + cardId));
        withdraw(creditCard, amount);
        domainEventPublisher.publish(new CardWithdrawn(cardId, amount));
    }
    

    public void withdraw(CreditCard creditCard, long amount) {
        if (thereIsMoneyToWithdraw(creditCard, amount)) {
        	creditCard.setUsedLimit(creditCard.getUsedLimit() + amount);
			log.info("creditCard = {}", creditCard);
			creditCardRepository.save(creditCard);
        } else {
            throw new NotEnoughMoneyException(creditCard.getId(), amount, availableBalance(creditCard));
        }
    }

    public long chargeBack(CreditCard creditCard, long amount) {
        return creditCard.getUsedLimit() - amount;
    }

    public long availableBalance(CreditCard creditCard) {
        return creditCard.getInitialLimit() - creditCard.getUsedLimit();
    }

    private boolean thereIsMoneyToWithdraw(CreditCard creditCard, long amount) {
        return availableBalance(creditCard) >= amount;
    }

}
