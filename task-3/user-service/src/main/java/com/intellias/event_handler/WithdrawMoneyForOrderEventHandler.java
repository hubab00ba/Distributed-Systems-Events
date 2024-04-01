package com.intellias.event_handler;

import static com.intellias.events.types.EventType.INSUFFICIENT_FUNDS_FOR_ORDER;
import static com.intellias.events.types.EventType.WITHDRAW_MONEY_FOR_ORDER_SUCCEEDED;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intellias.dto.EventEnvelope;
import com.intellias.events.handling.EventHandler;
import com.intellias.events.publishing.EventPublisher;
import com.intellias.events.topics.Channels;
import com.intellias.events.types.users.InsufficientFundsForOrder;
import com.intellias.events.types.users.WithdrawMoneyForOrder;
import com.intellias.events.types.users.WithdrawMoneyForOrderSucceeded;
import com.intellias.service.OrderPaymentService;

public class WithdrawMoneyForOrderEventHandler implements EventHandler<WithdrawMoneyForOrder> {
	private static final Logger LOGGER = LoggerFactory.getLogger(WithdrawMoneyForOrderEventHandler.class);

	private final OrderPaymentService orderPaymentService;
	private final EventPublisher eventPublisher;

	public WithdrawMoneyForOrderEventHandler(OrderPaymentService orderPaymentService, EventPublisher eventPublisher) {
		this.orderPaymentService = orderPaymentService;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void handleEvent(WithdrawMoneyForOrder event) {
		var userId = event.userId();
		var orderId = event.orderId();
		var amount = event.amount();
		LOGGER.info("Trying to withdrawn amount = {} from user {} for order = {}", amount, userId, orderId);
		var transactionExecutionResult = orderPaymentService.payForOrder(userId, orderId, amount);
		LOGGER.info("Transaction execution result = {}", transactionExecutionResult);
		switch (transactionExecutionResult) {
			case MONEY_WITHDRAWN, ALREADY_WITHDRAWN -> eventPublisher.publishEvent(
					orderId, new EventEnvelope(WITHDRAW_MONEY_FOR_ORDER_SUCCEEDED, new WithdrawMoneyForOrderSucceeded(orderId)),
					Channels.USER_SERVICE_RESPONSES_CHANNEL
			);
			case INSUFFICIENT_FUNDS -> eventPublisher.publishEvent(
					orderId, new EventEnvelope(INSUFFICIENT_FUNDS_FOR_ORDER, new InsufficientFundsForOrder(orderId)),
					Channels.USER_SERVICE_RESPONSES_CHANNEL
			);
		}
	}
}

