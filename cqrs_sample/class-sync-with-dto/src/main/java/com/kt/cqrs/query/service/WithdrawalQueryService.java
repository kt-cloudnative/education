package com.kt.cqrs.query.service;

import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.kt.cqrs.query.payload.WithdrawalDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalQueryService {

    private final JdbcTemplate jdbcTemplate;

	@SuppressWarnings("deprecation")
	public List<WithdrawalDto> withdraw(UUID cardId) {
		return jdbcTemplate.query("SELECT * FROM WITHDRAWAL WHERE CARD_ID = ?", new Object[]{cardId}, new BeanPropertyRowMapper<>(WithdrawalDto.class));
    }

}
