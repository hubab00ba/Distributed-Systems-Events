package com.intellias.event_handler;

import static com.intellias.events.topics.Channels.ITEM_SERVICE_RESPONSES_CHANNEL;
import static com.intellias.events.types.EventType.ITEM_RELEASED;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import com.intellias.dto.EventEnvelope;
import com.intellias.events.publishing.EventPublisher;
import com.intellias.events.types.items.ItemReleased;
import com.intellias.events.types.items.ReleaseItem;
import com.intellias.services.ItemReleaseService;

class TestReleaseItemEventHandler {

	public static final String ITEM_ID = "123";
	public static final String ORDER_ID = "2252";
	EventPublisher eventPublisher = mock(EventPublisher.class);
	ItemReleaseService itemReleaseService = mock(ItemReleaseService.class);

	ReleaseItemEventHandler releaseItemEventHandler = new ReleaseItemEventHandler(itemReleaseService, eventPublisher);

	@Test
	void handleEvent() {
		releaseItemEventHandler.handleEvent(new ReleaseItem(ITEM_ID, ORDER_ID));
		verify(itemReleaseService).releaseItemForOrder(ITEM_ID, ORDER_ID);
		verify(eventPublisher).publishEvent(
				ORDER_ID,
				new EventEnvelope(ITEM_RELEASED, new ItemReleased(ORDER_ID)),
				ITEM_SERVICE_RESPONSES_CHANNEL
		);
	}
}