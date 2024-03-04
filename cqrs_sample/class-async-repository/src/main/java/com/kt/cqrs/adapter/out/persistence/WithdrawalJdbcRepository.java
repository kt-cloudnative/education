package com.kt.cqrs.adapter.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.kt.cqrs.domain.port.Withdrawal;

public interface WithdrawalJdbcRepository extends CrudRepository<Withdrawal, UUID> {
	List<Withdrawal> findByCardId(UUID cardId);
}
