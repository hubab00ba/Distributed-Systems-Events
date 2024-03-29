package com.intellias.events.handling.impl;

import com.intellias.events.handling.EventHandler;
import com.intellias.serialization.Deserializer;
import com.intellias.serialization.Serializer;

public class TypeConvertingEventHandler<E> implements EventHandler<Object> {
	private final Deserializer deserializer;
	private final Serializer serializer;
	private final EventHandler<E> delegate;
	private final Class<E> eventType;

	public TypeConvertingEventHandler(Deserializer deserializer, Serializer serializer, EventHandler<E> delegate, Class<E> eventType) {
		this.deserializer = deserializer;
		this.serializer = serializer;
		this.delegate = delegate;
		this.eventType = eventType;
	}

	@Override
	public void handleEvent(Object event) {
		E typedEvent = deserializer.deserialize(serializer.serialize(event), eventType);
		delegate.handleEvent(typedEvent);
	}
}
