package com.intellias.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionTemplate;

import com.intellias.dao.ItemDetailsDAO;
import com.intellias.dao.ItemReservationsDAO;
import com.intellias.dto.ItemReservationResult;
import com.intellias.services.ItemReservationsService;

public class ItemReservationsServiceImpl implements ItemReservationsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemReservationsServiceImpl.class);

	private final ItemDetailsDAO itemDetailsDAO;
	private final ItemReservationsDAO itemReservationsDAO;
	private final TransactionTemplate transactionTemplate;

	public ItemReservationsServiceImpl(
			ItemReservationsDAO itemReservationsDAO,
			ItemDetailsDAO itemDetailsDAO,
			TransactionTemplate transactionTemplate
	) {
		this.itemReservationsDAO = itemReservationsDAO;
		this.itemDetailsDAO = itemDetailsDAO;
		this.transactionTemplate = transactionTemplate;
	}

	@Override
	public ItemReservationResult reserveItemForOrder(String itemId, String orderId) {
		LOGGER.info("Attempting to reserve item {} for order {}", itemId, orderId);
		var itemDetails = itemDetailsDAO.getItemDetails(itemId);
		LOGGER.info("Found item details {} by id = {}", itemDetails, itemId);
		if (itemReservationsDAO.doesReservationForOrderExists(itemId, orderId)) {
			LOGGER.info("Reservation for order already exists!");
			return new ItemReservationResult(ItemReservationResult.Status.ALREADY_RESERVED_FOR_ORDER, itemDetails.price());
		}
		if (itemDetails.isOutOfStock()) {
			return new ItemReservationResult(ItemReservationResult.Status.OUT_OF_STOCK, itemDetails.price());
		}
		transactionTemplate.executeWithoutResult(status -> {
			itemReservationsDAO.reserveItemForOrder(itemId, orderId);
			itemDetailsDAO.updateAvailableQuantity(itemId, -1);
		});
		return new ItemReservationResult(ItemReservationResult.Status.RESERVED, itemDetails.price());
	}
}
