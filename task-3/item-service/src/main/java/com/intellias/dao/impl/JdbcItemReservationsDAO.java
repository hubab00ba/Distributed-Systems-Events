package com.intellias.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;

import com.intellias.dao.ItemReservationsDAO;

public class JdbcItemReservationsDAO implements ItemReservationsDAO {
	protected static final String CHECK_RESERVATION_PRESENCE_QUERY = "SELECT 1 FROM ITEM_RESERVATIONS i WHERE i.ITEM_ID = ? AND i.ORDER_ID = ?";

	private final JdbcTemplate jdbcTemplate;

	public JdbcItemReservationsDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public boolean doesReservationForOrderExists(String itemId, String orderId) {
		return !jdbcTemplate.queryForList(CHECK_RESERVATION_PRESENCE_QUERY, itemId, orderId).isEmpty();
	}

	@Override
	public void reserveItemForOrder(String itemId, String orderId) {
		jdbcTemplate.update("INSERT INTO ITEM_RESERVATIONS(ITEM_ID, ORDER_ID) VALUES (?, ?)", itemId, orderId);
	}

	@Override
	public void removeReservationForOrder(String itemId, String orderId) {
		jdbcTemplate.update("DELETE FROM ITEM_RESERVATIONS WHERE ITEM_ID = ? AND ORDER_ID = ?", itemId, orderId);
	}
}
