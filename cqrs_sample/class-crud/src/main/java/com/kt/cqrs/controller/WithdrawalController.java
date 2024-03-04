package com.kt.cqrs.controller;

import java.util.List;
import java.util.UUID;
import javax.websocket.server.PathParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kt.cqrs.repository.entity.Withdrawal;
import com.kt.cqrs.service.WithdrawalService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/withdrawal")
@RequiredArgsConstructor
class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @PostMapping
    ResponseEntity<?> withdraw(@RequestBody Withdrawal withdrawal) {
        withdrawalService.withdraw(withdrawal.getCardId(), withdrawal.getAmount());
        return ResponseEntity.ok().build();
    }

	@GetMapping
    ResponseEntity<List<Withdrawal>> withdrawal(@PathParam("cardId") String cardId) {
        return ResponseEntity.ok().body(withdrawalService.withdraw(UUID.fromString(cardId)));
    }
}

