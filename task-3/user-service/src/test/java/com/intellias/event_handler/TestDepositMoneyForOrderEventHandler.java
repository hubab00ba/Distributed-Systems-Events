package com.intellias.event_handler;

import static com.intellias.events.topics.Channels.USER_SERVICE_RESPONSES_CHANNEL;
import static com.intellias.events.types.EventType.DEPOSITED_MONEY_FOR_ORDER;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import com.intellias.dto.EventEnvelope;
import com.intellias.events.publishing.EventPublisher;
import com.intellias.events.types.users.DepositMoneyForOrder;
import com.intellias.events.types.users.DepositedMoneyForOrder;
import com.intellias.service.OrderDepositService;

class TestDepositMoneyForOrderEventHandler {

	public static final String USER_ID = "2525";
	public static final String ORDER_ID = "123";
	OrderDepositService orderDepositService = mock(OrderDepositService.class);
	EventPublisher eventPublisher = mock(EventPublisher.class);

	DepositMoneyForOrderEventHandler depositMoneyForOrderEventHandler =
			new DepositMoneyForOrderEventHandler(orderDepositService, eventPublisher);

	@Test
	void handleEvent() {
		depositMoneyForOrderEventHandler.handleEvent(new DepositMoneyForOrder(USER_ID, ORDER_ID));
		verify(orderDepositService).depositMoneyBack(ORDER_ID, USER_ID);
		verify(eventPublisher)
				.publishEvent(
						ORDER_ID,
						new EventEnvelope(DEPOSITED_MONEY_FOR_ORDER, new DepositedMoneyForOrder(ORDER_ID)),
						USER_SERVICE_RESPONSES_CHANNEL
				);
	}
}