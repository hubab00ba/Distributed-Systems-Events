package com.intellias.event_handler;

import static com.intellias.events.topics.Channels.ITEM_SERVICE_RESPONSES_CHANNEL;
import static com.intellias.events.types.EventType.ITEM_RESERVATION_FAILED;
import static com.intellias.events.types.EventType.ITEM_RESERVED;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intellias.dto.EventEnvelope;
import com.intellias.events.handling.EventHandler;
import com.intellias.events.publishing.EventPublisher;
import com.intellias.events.types.items.ItemReservationFailed;
import com.intellias.events.types.items.ItemReservationSucceeded;
import com.intellias.events.types.items.ReserveItem;
import com.intellias.services.ItemReservationsService;

public class ReserveItemEventHandler implements EventHandler<ReserveItem> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReserveItemEventHandler.class);

	private final ItemReservationsService itemReservationsService;
	private final EventPublisher eventPublisher;

	public ReserveItemEventHandler(ItemReservationsService itemReservationsService, EventPublisher eventPublisher) {
		this.itemReservationsService = itemReservationsService;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void handleEvent(ReserveItem event) {
		var itemId = event.itemId();
		var orderId = event.orderId();
		LOGGER.info("Trying to reserve item {} for order {}", itemId, orderId);
		var reservationResult = itemReservationsService.reserveItemForOrder(itemId, orderId);
		LOGGER.info("Reservation result = {}", reservationResult);
		switch (reservationResult.status()) {
			case RESERVED, ALREADY_RESERVED_FOR_ORDER -> {
				LOGGER.info("Reserved or was already reserved");
				eventPublisher.publishEvent(
						orderId, new EventEnvelope(ITEM_RESERVED, new ItemReservationSucceeded(orderId, reservationResult.currentPrice())),
						ITEM_SERVICE_RESPONSES_CHANNEL
				);
			}
			case OUT_OF_STOCK -> {
				LOGGER.info("Item is out of stock...");
				eventPublisher.publishEvent(
						orderId, new EventEnvelope(ITEM_RESERVATION_FAILED, new ItemReservationFailed(orderId)),
						ITEM_SERVICE_RESPONSES_CHANNEL
				);
			}
		}
	}
}
