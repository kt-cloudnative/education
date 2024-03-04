package com.kt.cqrs.command.repository.entity;

import org.springframework.data.annotation.Id;
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
@Table("credit_card")
public class CreditCard {

    @Id 
    private String id;
    private long initialLimit;
    private long usedLimit;

    public CreditCard(long limit) {
        this.initialLimit = limit;
    }

}
