package com.kt.cqrs.adapter.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.kt.cqrs.domain.port.Withdrawal;
import com.kt.cqrs.domain.port.WithdrawalRepository;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class WithdrawalRepositoryAdapter implements WithdrawalRepository {

    private final WithdrawalJdbcRepository withdrawalJdbcRepository;

    @Override
    public List<Withdrawal> list(UUID cardId) {
        return withdrawalJdbcRepository.findByCardId(cardId);
    }
}
