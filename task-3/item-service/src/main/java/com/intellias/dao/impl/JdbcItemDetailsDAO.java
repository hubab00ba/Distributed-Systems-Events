package com.intellias.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;

import com.intellias.dao.ItemDetailsDAO;
import com.intellias.dto.ItemDetails;

public class JdbcItemDetailsDAO implements ItemDetailsDAO {
	protected static final String FETCH_ITEM_DETAILS = "SELECT PRICE, QUANTITY FROM ITEMS WHERE ID = ?";

	private final JdbcTemplate jdbcTemplate;

	public JdbcItemDetailsDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public ItemDetails getItemDetails(String itemId) {
		return jdbcTemplate.queryForObject(FETCH_ITEM_DETAILS, this::toItemDetails, itemId);
	}

	@Override
	public void updateAvailableQuantity(String itemId, int quantityDelta) {
		jdbcTemplate.update("UPDATE ITEMS SET QUANTITY = QUANTITY + ? WHERE ID = ?", quantityDelta, itemId);
	}

	private ItemDetails toItemDetails(ResultSet rs, int rowNum) throws SQLException {
		return new ItemDetails(rs.getBigDecimal("PRICE"), rs.getInt("QUANTITY"));
	}

}
