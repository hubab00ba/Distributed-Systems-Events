package com.intellias.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import com.intellias.dao.OrdersDAO;
import com.intellias.dto.OrderStatus;

public class JdbcOrdersDAO implements OrdersDAO {
	public static final String INSERT_NEW_ORDER = "INSERT INTO ORDERS(ID, USER_ID, ITEM, STATUS) VALUES (?, ?, ?, ?)";
	private static final String CHECK_IF_ORDER_EXISTS = "SELECT 1 FROM ORDERS WHERE ID = ?";
	private final TransactionTemplate transactionTemplate;
	private final JdbcTemplate jdbcTemplate;

	public JdbcOrdersDAO(TransactionTemplate transactionTemplate, JdbcTemplate jdbcTemplate) {
		this.transactionTemplate = transactionTemplate;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void initOrder(String orderId, String itemId, String userId) {
		if (!isOrderCreated(orderId)) {
			transactionTemplate.executeWithoutResult(res -> jdbcTemplate.update(
					INSERT_NEW_ORDER,
					orderId,
					userId,
					itemId,
					OrderStatus.WAITING_FOR_ITEM_RESERVATION.name()
			));
		}
	}

	@Override
	public String getOrderCreator(String orderId) {
		return jdbcTemplate.queryForObject("SELECT USER_ID FROM ORDERS WHERE ID = ?", String.class, orderId);
	}

	@Override
	public String getItemIdByOrderId(String orderId) {
		return jdbcTemplate.queryForObject("SELECT ITEM FROM ORDERS WHERE ID = ?", String.class, orderId);
	}

	@Override
	public void changeStatusOfOrder(String orderId, OrderStatus newStatus) {
		jdbcTemplate.update("UPDATE ORDERS SET STATUS = ? WHERE ID = ?", newStatus.name(), orderId);
	}

	private boolean isOrderCreated(String orderId) {
		return !jdbcTemplate.queryForList(CHECK_IF_ORDER_EXISTS, String.class, orderId).isEmpty();
	}

}
