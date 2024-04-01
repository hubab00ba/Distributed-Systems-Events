package com.intellias.events.publishing;

public interface EventPublisher {
	void publishEvent(String key, Object event, String channel);
}
