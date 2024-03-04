package com.kt.cqrs.query.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kt.cqrs.query.repository.WithdrawalRepository;
import com.kt.cqrs.query.repository.entity.Withdrawal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalQueryService {

    private final WithdrawalRepository withdrawalRepository;

	public List<Withdrawal> withdraw(UUID cardId) {
		return withdrawalRepository.findByCardId(cardId);
	}
}
