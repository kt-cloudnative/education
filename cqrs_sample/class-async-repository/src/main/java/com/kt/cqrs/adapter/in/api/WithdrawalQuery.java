package com.kt.cqrs.adapter.in.api;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalQuery {
    private UUID cardId;
    private long amount;
}
