package com.intellias.event_handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intellias.dao.OrdersDAO;
import com.intellias.dto.OrderStatus;
import com.intellias.events.handling.EventHandler;
import com.intellias.events.types.items.ItemReservationFailed;

public class ItemForOrderNotReservedEventHandler implements EventHandler<ItemReservationFailed> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemForOrderNotReservedEventHandler.class);

	private final OrdersDAO ordersDAO;

	public ItemForOrderNotReservedEventHandler(OrdersDAO ordersDAO) {
		this.ordersDAO = ordersDAO;
	}

	@Override
	public void handleEvent(ItemReservationFailed event) {
		var orderId = event.orderId();
		LOGGER.info("Failed to reserve item for order {}", orderId);
		ordersDAO.changeStatusOfOrder(orderId, OrderStatus.OUT_OF_STOCK);
	}
}
