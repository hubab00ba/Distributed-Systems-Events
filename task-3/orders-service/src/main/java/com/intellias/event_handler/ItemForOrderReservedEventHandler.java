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
import com.intellias.events.types.items.ItemReservationSucceeded;
import com.intellias.events.types.users.WithdrawMoneyForOrder;

public class ItemForOrderReservedEventHandler implements EventHandler<ItemReservationSucceeded> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemForOrderReservedEventHandler.class);

	private final OrdersDAO ordersDAO;
	private final EventPublisher eventPublisher;

	public ItemForOrderReservedEventHandler(OrdersDAO ordersDAO, EventPublisher eventPublisher) {
		this.ordersDAO = ordersDAO;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void handleEvent(ItemReservationSucceeded event) {
		var orderId = event.orderId();
		var price = event.price();
		LOGGER.info("Item reservation for order {} at price = {} was successful!", orderId, price);
		ordersDAO.changeStatusOfOrder(orderId, OrderStatus.WAITING_FOR_PAYMENT);
		var userId = ordersDAO.getOrderCreator(orderId);
		LOGGER.info("Requesting payment of amount = {} from user {} for order {}", price, userId, orderId);
		eventPublisher.publishEvent(
				userId,
				new EventEnvelope(EventType.WITHDRAW_MONEY_FOR_ORDER, new WithdrawMoneyForOrder(userId, orderId, price)),
				Channels.USER_SERVICE_REQUESTS_CHANNEL
		);
	}
}
