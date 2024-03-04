package com.kt.cqrs.query.controller;

import java.util.List;
import java.util.UUID;

import javax.websocket.server.PathParam;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.cqrs.query.repository.entity.Withdrawal;
import com.kt.cqrs.query.service.WithdrawalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/withdrawal")
@RequiredArgsConstructor
class WithdrawalController {

	private final WithdrawalService withdrawalService;

    @GetMapping
    ResponseEntity<List<Withdrawal>> withdrawals(@PathParam("cardId") String cardId) {
    	 return ResponseEntity.ok().body(withdrawalService.withdraw(UUID.fromString(cardId)));
    }


}

