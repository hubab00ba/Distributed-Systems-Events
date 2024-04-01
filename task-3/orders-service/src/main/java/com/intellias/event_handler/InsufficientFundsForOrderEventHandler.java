package com.intellias.event_handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intellias.dao.OrdersDAO;
import com.intellias.dto.EventEnvelope;
import com.intellias.dto.OrderStatus;
import com.intellias.events.handling.EventHandler;
import com.intellias.events.publishing.EventPublisher;
import com.intellias.events.topics.Channels;
import com.intellias.events.types.EventType;
import com.intellias.events.types.items.ReleaseItem;
import com.intellias.events.types.users.InsufficientFundsForOrder;

public class InsufficientFundsForOrderEventHandler implements EventHandler<InsufficientFundsForOrder> {
	private static final Logger LOGGER = LoggerFactory.getLogger(InsufficientFundsForOrderEventHandler.class);

	private final OrdersDAO ordersDAO;
	private final EventPublisher eventPublisher;

	public InsufficientFundsForOrderEventHandler(OrdersDAO ordersDAO, EventPublisher eventPublisher) {
		this.ordersDAO = ordersDAO;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void handleEvent(InsufficientFundsForOrder event) {
		var orderId = event.orderId();
		LOGGER.info("User doesn't have enough funds for order {}", orderId);
		ordersDAO.changeStatusOfOrder(orderId, OrderStatus.INSUFFICIENT_FUNDS);
		var itemId = ordersDAO.getItemIdByOrderId(orderId);
		LOGGER.info("Releasing item {} because order {} is cancelled", itemId, orderId);
		eventPublisher.publishEvent(
				itemId,
				new EventEnvelope(EventType.RELEASE_ITEM, new ReleaseItem(itemId, orderId)),
				Channels.ITEM_SERVICE_REQUESTS_CHANNEL
		);
	}
}
