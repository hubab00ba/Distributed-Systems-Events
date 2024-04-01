package com.intellias.consumer;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intellias.dto.EventEnvelope;
import com.intellias.events.handling.EventHandler;
import com.intellias.process.ProcessCondition;
import com.intellias.serialization.Deserializer;

public class KafkaEventListener implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaEventListener.class);

	private final Map<String, Map<String, EventHandler<Object>>> eventHandlerByEventTypeByTopic;
	private final KafkaConsumer<String, String> kafkaConsumer;
	private final ProcessCondition processCondition;
	private final Duration pollDuration;
	private final Deserializer deserializer;

	public KafkaEventListener(
			Map<String, Map<String, EventHandler<Object>>> eventHandlerByEventTypeByTopic,
			KafkaConsumer<String, String> kafkaConsumer,
			ProcessCondition processCondition,
			Duration pollDuration,
			Deserializer deserializer
	) {
		this.eventHandlerByEventTypeByTopic = eventHandlerByEventTypeByTopic;
		this.kafkaConsumer = kafkaConsumer;
		this.processCondition = processCondition;
		this.pollDuration = pollDuration;
		this.deserializer = deserializer;
	}

	@Override
	public void run() {
		var topics = eventHandlerByEventTypeByTopic.keySet();
		LOGGER.info("Subscribing to topics = {}", topics);
		kafkaConsumer.subscribe(topics);
		while (processCondition.shouldRun()) {
			var records = kafkaConsumer.poll(pollDuration);
			LOGGER.info("Processing new batch of events");
			eventHandlerByEventTypeByTopic.forEach((topic, map) -> records.records(topic).forEach(record -> {
				var recordValue = record.value();
				LOGGER.info("Processing record {}", record);
				var eventEnvelope =
						deserializer.deserialize(recordValue.getBytes(StandardCharsets.UTF_8), EventEnvelope.class);
				var eventType = eventEnvelope.eventType();
				var eventHandler = map.get(eventType);
				if (eventHandler == null) {
					LOGGER.info("Going to ignore event of type = {}", eventType);
				} else {
					eventHandler.handleEvent(eventEnvelope.payload());
				}
			}));
			kafkaConsumer.commitSync();
		}
	}
}
