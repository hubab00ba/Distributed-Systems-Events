package com.intellias.event_handler;

import static com.intellias.events.types.EventType.INSUFFICIENT_FUNDS_FOR_ORDER;
import static com.intellias.events.types.EventType.WITHDRAW_MONEY_FOR_ORDER_SUCCEEDED;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.intellias.dto.EventEnvelope;
import com.intellias.dto.PaymentResult;
import com.intellias.events.publishing.EventPublisher;
import com.intellias.events.topics.Channels;
import com.intellias.events.types.users.InsufficientFundsForOrder;
import com.intellias.events.types.users.WithdrawMoneyForOrder;
import com.intellias.events.types.users.WithdrawMoneyForOrderSucceeded;
import com.intellias.service.OrderPaymentService;

class TestWithdrawMoneyForOrderEventHandler {
	private static final BigDecimal AMOUNT = BigDecimal.ONE;
	private static final String ORDER_ID = "25235";
	private static final String USER_ID = "123";

	OrderPaymentService orderPaymentService = mock(OrderPaymentService.class);
	EventPublisher eventPublisher = mock(EventPublisher.class);

	WithdrawMoneyForOrderEventHandler withdrawMoneyForOrderEventHandler =
			new WithdrawMoneyForOrderEventHandler(orderPaymentService, eventPublisher);

	@Test
	void handleEventGivenMoneyWereSuccessfullyWithdrawn() {
		when(orderPaymentService.payForOrder(USER_ID, ORDER_ID, AMOUNT)).thenReturn(PaymentResult.MONEY_WITHDRAWN);
		withdrawMoneyForOrderEventHandler.handleEvent(new WithdrawMoneyForOrder(USER_ID, ORDER_ID, AMOUNT));
		verify(eventPublisher).publishEvent(
				ORDER_ID, new EventEnvelope(WITHDRAW_MONEY_FOR_ORDER_SUCCEEDED, new WithdrawMoneyForOrderSucceeded(ORDER_ID)),
				Channels.USER_SERVICE_RESPONSES_CHANNEL
		);
	}

	@Test
	void handleEventGivenMoneyWereNotWithdrawn() {
		when(orderPaymentService.payForOrder(USER_ID, ORDER_ID, AMOUNT)).thenReturn(PaymentResult.INSUFFICIENT_FUNDS);
		withdrawMoneyForOrderEventHandler.handleEvent(new WithdrawMoneyForOrder(USER_ID, ORDER_ID, AMOUNT));
		verify(eventPublisher).publishEvent(
				ORDER_ID, new EventEnvelope(INSUFFICIENT_FUNDS_FOR_ORDER, new InsufficientFundsForOrder(ORDER_ID)),
				Channels.USER_SERVICE_RESPONSES_CHANNEL
		);
	}

}
