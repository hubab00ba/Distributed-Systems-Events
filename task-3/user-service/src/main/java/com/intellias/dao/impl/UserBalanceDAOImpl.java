package com.intellias.dao.impl;

import java.math.BigDecimal;

import org.springframework.jdbc.core.JdbcTemplate;

import com.intellias.dao.UserBalanceDAO;

public class UserBalanceDAOImpl implements UserBalanceDAO {
	private static final String GET_CURRENT_BALANCE_QUERY = "SELECT BALANCE FROM USERS WHERE ID = ?";

	private final JdbcTemplate jdbcTemplate;

	public UserBalanceDAOImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public BigDecimal fetchBalanceOfUser(String userId) {
		return jdbcTemplate.queryForObject(GET_CURRENT_BALANCE_QUERY, BigDecimal.class, userId);
	}

	@Override
	public void updateUserBalance(String userId, BigDecimal delta) {

	}
}
