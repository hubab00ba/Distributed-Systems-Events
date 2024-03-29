package com.intellias.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionTemplate;

import com.intellias.dao.ItemDetailsDAO;
import com.intellias.dao.ItemReservationsDAO;
import com.intellias.services.ItemReleaseService;

public class ItemReleaseServiceImpl implements ItemReleaseService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemReleaseServiceImpl.class);

	private final ItemReservationsDAO itemReservationsDAO;
	private final ItemDetailsDAO itemDetailsDAO;
	private final TransactionTemplate transactionTemplate;

	public ItemReleaseServiceImpl(
			ItemReservationsDAO itemReservationsDAO,
			ItemDetailsDAO itemDetailsDAO,
			TransactionTemplate transactionTemplate
	) {
		this.itemReservationsDAO = itemReservationsDAO;
		this.itemDetailsDAO = itemDetailsDAO;
		this.transactionTemplate = transactionTemplate;
	}

	@Override
	public void releaseItemForOrder(String itemId, String orderId) {
		LOGGER.info("Releasing item {} for order {}", itemId, orderId);
		if (itemReservationsDAO.doesReservationForOrderExists(itemId, orderId)) {
			LOGGER.info("Reservation exists! Releasing item");
			transactionTemplate.executeWithoutResult(status -> {
				itemReservationsDAO.removeReservationForOrder(itemId, orderId);
				itemDetailsDAO.updateAvailableQuantity(itemId, 1);
			});
		} else {
			LOGGER.info("Reservation was already removed!");
		}
	}
}
