package com.kt.cqrs.query.controller;

import java.util.List;
import java.util.UUID;

import javax.websocket.server.PathParam;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.cqrs.query.payload.WithdrawalDto;
import com.kt.cqrs.query.service.WithdrawalQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/withdrawal")
@RequiredArgsConstructor
class WithdrawalQueryController {

    private final WithdrawalQueryService withdrawalQueryService;


    @GetMapping
    ResponseEntity<List<WithdrawalDto>> withdrawals(@PathParam("cardId") String cardId) {
        return ResponseEntity.ok().body(withdrawalQueryService.withdraw(UUID.fromString(cardId)));
    }

}

