package com.intellias.dao.impl;

import java.math.BigDecimal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import com.intellias.dao.UserTransactionsDAO;

public class JdbcUserTransactionsDAO implements UserTransactionsDAO {
	private static final String CHECK_TRANSACTION_PRESENCE_QUERY = "SELECT 1 FROM USER_TRANSACTIONS WHERE USER_ID = ? AND ORDER_ID = ?";
	public static final String FETCH_TRANSACTION_AMOUNT = "SELECT AMOUNT FROM USER_TRANSACTIONS WHERE USER_ID = ? AND ORDER_ID = ?";

	private final JdbcTemplate jdbcTemplate;

	public JdbcUserTransactionsDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public boolean doesTransactionExist(String userId, String orderId) {
		return !jdbcTemplate.queryForList(CHECK_TRANSACTION_PRESENCE_QUERY, userId, orderId).isEmpty();
	}

	@Override
	public void createTransaction(String userId, String orderId, BigDecimal amount) {
		jdbcTemplate.update("INSERT INTO USER_TRANSACTIONS(USER_ID, ORDER_ID, AMOUNT) VALUES (?, ?, ?)", userId, orderId, amount);
	}

	@Override
	public BigDecimal getTransactionAmount(String userId, String orderId) {
		return jdbcTemplate.queryForObject(FETCH_TRANSACTION_AMOUNT, BigDecimal.class, userId, orderId);
	}

	@Override
	public void removeTransaction(String userId, String orderId) {
		jdbcTemplate.update("DELETE FROM USER_TRANSACTIONS WHERE USER_ID = ? AND ORDER_ID = ?", userId, orderId);
	}

}
