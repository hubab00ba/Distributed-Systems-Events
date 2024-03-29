package com.intellias.event_handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intellias.dao.OrdersDAO;
import com.intellias.dto.EventEnvelope;
import com.intellias.events.handling.EventHandler;
import com.intellias.events.publishing.EventPublisher;
import com.intellias.events.topics.Channels;
import com.intellias.events.types.EventType;
import com.intellias.events.types.items.ReserveItem;
import com.intellias.events.types.orders.PrepareOrder;

public class PrepareOrderEventHandler implements EventHandler<PrepareOrder> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PrepareOrderEventHandler.class);

	private final OrdersDAO ordersDAO;
	private final EventPublisher eventPublisher;

	public PrepareOrderEventHandler(OrdersDAO ordersDAO, EventPublisher eventPublisher) {
		this.ordersDAO = ordersDAO;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void handleEvent(PrepareOrder event) {
		var itemId = event.itemId();
		var orderId = event.orderId();
		var userId = event.userId();
		LOGGER.info("Preparing order {} for user {} and item {}", orderId, userId, itemId);
		ordersDAO.initOrder(orderId, itemId, userId);
		LOGGER.info("Trying to reserve item = {}...", itemId);
		eventPublisher.publishEvent(
				itemId,
				new EventEnvelope(EventType.RESERVE_ITEM, new ReserveItem(itemId, orderId)),
				Channels.ITEM_SERVICE_REQUESTS_CHANNEL
		);
	}
}
