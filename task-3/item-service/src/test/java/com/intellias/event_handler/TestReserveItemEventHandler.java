package com.intellias.event_handler;

import static com.intellias.events.topics.Channels.ITEM_SERVICE_RESPONSES_CHANNEL;
import static com.intellias.events.types.EventType.ITEM_RESERVATION_FAILED;
import static com.intellias.events.types.EventType.ITEM_RESERVED;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.intellias.dto.EventEnvelope;
import com.intellias.dto.ItemReservationResult;
import com.intellias.events.publishing.EventPublisher;
import com.intellias.events.types.items.ItemReservationFailed;
import com.intellias.events.types.items.ItemReservationSucceeded;
import com.intellias.events.types.items.ReserveItem;
import com.intellias.services.ItemReservationsService;

class TestReserveItemEventHandler {
	private static final String ITEM_ID = "123";
	private static final String ORDER_ID = "2525";
	private static final BigDecimal PRICE = BigDecimal.ONE;

	ItemReservationsService itemReservationsService = mock(ItemReservationsService.class);
	EventPublisher eventPublisher = mock(EventPublisher.class);

	ReserveItemEventHandler reserveItemEventHandler = new ReserveItemEventHandler(itemReservationsService, eventPublisher);

	@Test
	void handleEventGivenReservationSuccessful() {
		when(itemReservationsService.reserveItemForOrder(ITEM_ID, ORDER_ID))
				.thenReturn(new ItemReservationResult(ItemReservationResult.Status.RESERVED, PRICE));
		reserveItemEventHandler.handleEvent(new ReserveItem(ITEM_ID, ORDER_ID));
		verify(eventPublisher).publishEvent(
				ORDER_ID, new EventEnvelope(ITEM_RESERVED, new ItemReservationSucceeded(ORDER_ID, PRICE)),
				ITEM_SERVICE_RESPONSES_CHANNEL
		);
	}

	@Test
	void handleEventGivenItemOutOfStock() {
		when(itemReservationsService.reserveItemForOrder(ITEM_ID, ORDER_ID))
				.thenReturn(new ItemReservationResult(ItemReservationResult.Status.OUT_OF_STOCK, PRICE));
		reserveItemEventHandler.handleEvent(new ReserveItem(ITEM_ID, ORDER_ID));
		verify(eventPublisher).publishEvent(
				ORDER_ID, new EventEnvelope(ITEM_RESERVATION_FAILED, new ItemReservationFailed(ORDER_ID)),
				ITEM_SERVICE_RESPONSES_CHANNEL
		);
	}

}
