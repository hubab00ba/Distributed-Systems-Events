package com.intellias.events.handling;

public interface EventHandler<E> {
	void handleEvent(E event);
}
