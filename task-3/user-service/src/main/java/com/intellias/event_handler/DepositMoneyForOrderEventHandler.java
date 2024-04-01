package com.intellias.event_handler;

import static com.intellias.events.topics.Channels.USER_SERVICE_RESPONSES_CHANNEL;
import static com.intellias.events.types.EventType.DEPOSITED_MONEY_FOR_ORDER;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intellias.dto.EventEnvelope;
import com.intellias.events.handling.EventHandler;
import com.intellias.events.publishing.EventPublisher;
import com.intellias.events.types.users.DepositMoneyForOrder;
import com.intellias.events.types.users.DepositedMoneyForOrder;
import com.intellias.service.OrderDepositService;

public class DepositMoneyForOrderEventHandler implements EventHandler<DepositMoneyForOrder> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DepositMoneyForOrderEventHandler.class);

	private final OrderDepositService orderDepositService;
	private final EventPublisher eventPublisher;

	public DepositMoneyForOrderEventHandler(OrderDepositService orderDepositService, EventPublisher eventPublisher) {
		this.orderDepositService = orderDepositService;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void handleEvent(DepositMoneyForOrder event) {
		var orderId = event.orderId();
		var userId = event.userId();
		LOGGER.info("Depositing money back for user {} because order {} was not executed...", userId, orderId);
		orderDepositService.depositMoneyBack(orderId, userId);
		eventPublisher.publishEvent(
				orderId, new EventEnvelope(DEPOSITED_MONEY_FOR_ORDER, new DepositedMoneyForOrder(orderId)), USER_SERVICE_RESPONSES_CHANNEL);
	}
}
