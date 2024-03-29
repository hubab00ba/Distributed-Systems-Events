package com.intellias.event_handler;

import static com.intellias.events.topics.Channels.ITEM_SERVICE_RESPONSES_CHANNEL;
import static com.intellias.events.types.EventType.ITEM_RELEASED;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intellias.dto.EventEnvelope;
import com.intellias.events.handling.EventHandler;
import com.intellias.events.publishing.EventPublisher;
import com.intellias.events.types.items.ItemReleased;
import com.intellias.events.types.items.ReleaseItem;
import com.intellias.services.ItemReleaseService;

public class ReleaseItemEventHandler implements EventHandler<ReleaseItem> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReleaseItemEventHandler.class);

	private final ItemReleaseService itemReleaseService;
	private final EventPublisher eventPublisher;

	public ReleaseItemEventHandler(ItemReleaseService itemReleaseService, EventPublisher eventPublisher) {
		this.itemReleaseService = itemReleaseService;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void handleEvent(ReleaseItem event) {
		var itemId = event.itemId();
		var orderId = event.orderId();
		LOGGER.info("Releasing one item {} previously reserved for order {}", itemId, orderId);
		itemReleaseService.releaseItemForOrder(itemId, orderId);
		eventPublisher.publishEvent(orderId, new EventEnvelope(ITEM_RELEASED, new ItemReleased(orderId)), ITEM_SERVICE_RESPONSES_CHANNEL);
	}
}
