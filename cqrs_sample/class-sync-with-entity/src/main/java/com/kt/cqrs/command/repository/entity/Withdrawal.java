package com.kt.cqrs.command.repository.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table("WITHDRAWAL")
public class Withdrawal implements Persistable<UUID>{

    @Transient
    private boolean isNew = false;
    
    @Id
    private UUID id;
    private long amount;
    private UUID cardId;
    
    public static Withdrawal newWithdrawal(UUID id, long amount, UUID cardId) {
    	Withdrawal withdrawal = new Withdrawal(true, id, amount, cardId);
        return withdrawal;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

}
