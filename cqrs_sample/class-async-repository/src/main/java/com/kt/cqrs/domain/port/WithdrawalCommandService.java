package com.kt.cqrs.domain.port;

import java.util.UUID;

public interface WithdrawalCommandService {
	public void withdraw(UUID cardId, long amount);
}
