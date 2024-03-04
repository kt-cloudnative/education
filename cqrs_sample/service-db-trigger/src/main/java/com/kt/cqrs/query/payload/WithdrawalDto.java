package com.kt.cqrs.query.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalDto {
    private UUID cardId;
    private long amount;

}
