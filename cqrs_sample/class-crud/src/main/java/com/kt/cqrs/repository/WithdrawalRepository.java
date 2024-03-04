package com.kt.cqrs.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import com.kt.cqrs.repository.entity.Withdrawal;

public interface WithdrawalRepository extends CrudRepository<Withdrawal, UUID> {
    List<Withdrawal> findByCardId(UUID cardId);
}
