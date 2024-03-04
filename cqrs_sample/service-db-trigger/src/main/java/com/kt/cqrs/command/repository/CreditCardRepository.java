package com.kt.cqrs.command.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.kt.cqrs.command.repository.entity.CreditCard;

public interface CreditCardRepository extends CrudRepository<CreditCard, UUID> {

}
