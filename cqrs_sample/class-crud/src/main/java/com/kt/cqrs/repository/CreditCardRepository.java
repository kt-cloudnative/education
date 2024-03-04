package com.kt.cqrs.repository;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import com.kt.cqrs.repository.entity.CreditCard;

public interface CreditCardRepository extends CrudRepository<CreditCard, UUID> {
}
