package com.kt.cqrs.adapter.out.persistence;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import com.kt.cqrs.domain.port.CreditCard;
import com.kt.cqrs.domain.port.CreditCardRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class CreditCardRepositoryAdapter implements CreditCardRepository {

    private final CreditCardJdbcRepository creditCardJdbcRepository;

    @Override
    public Optional<CreditCard> load(UUID cardId) {
        return creditCardJdbcRepository.findById(cardId);
    }

    @Override
    public void save(CreditCard record) {
    	creditCardJdbcRepository.save(record);
    }
}
