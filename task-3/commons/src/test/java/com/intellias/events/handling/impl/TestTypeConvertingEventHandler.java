package com.intellias.events.handling.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.intellias.events.handling.EventHandler;
import com.intellias.serialization.Deserializer;
import com.intellias.serialization.Serializer;

@SuppressWarnings("unchecked")
class TestTypeConvertingEventHandler {
	private static final String NUM_STR = "5";
	private static final byte[] BYTES = {2, 1, 6};
	private static final Class<Integer> TYPE = Integer.class;

	Deserializer deserializer = mock(Deserializer.class);
	Serializer serializer = mock(Serializer.class);
	EventHandler<Integer> delegate = mock(EventHandler.class);

	TypeConvertingEventHandler<Integer> typeConvertingEventHandler = new TypeConvertingEventHandler<>(
			deserializer,
			serializer,
			delegate,
			TYPE
	);

	@Test
	void handleEvent() {
		when(serializer.serialize(NUM_STR)).thenReturn(BYTES);
		when(deserializer.deserialize(BYTES, TYPE)).thenReturn(5);
		typeConvertingEventHandler.handleEvent(NUM_STR);
		verify(delegate).handleEvent(5);
	}
}
