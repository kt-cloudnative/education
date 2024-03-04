package com.kt.cqrs.domain.port;

import java.util.List;
import java.util.UUID;

public interface WithdrawalRepository {

    List<Withdrawal> list(UUID cardId);

}
