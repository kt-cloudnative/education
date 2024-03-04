package com.kt.cqrs.adapter.out.persistence;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.kt.cqrs.domain.port.CreditCard;

public interface CreditCardJdbcRepository extends CrudRepository<CreditCard, UUID> {
}
