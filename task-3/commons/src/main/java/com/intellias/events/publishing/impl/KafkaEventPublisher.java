package com.intellias.events.publishing.impl;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intellias.events.publishing.EventPublisher;
import com.intellias.serialization.Serializer;

public class KafkaEventPublisher implements EventPublisher {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaEventPublisher.class);

	private final Serializer serializer;
	private final KafkaProducer<String, String> kafkaProducer;

	public KafkaEventPublisher(Serializer serializer, KafkaProducer<String, String> kafkaProducer) {
		this.serializer = serializer;
		this.kafkaProducer = kafkaProducer;
	}

	@Override
	public void publishEvent(String key, Object event, String channel) {
		try {
			var eventStr = new String(serializer.serialize(event), StandardCharsets.UTF_8);
			var recordMetadata = kafkaProducer.send(new ProducerRecord<>(channel, key, eventStr)).get();
			LOGGER.info("Event {} sent to topic {}. Metadata = {}", eventStr, channel, recordMetadata);
			kafkaProducer.flush();
		}
		catch (InterruptedException | ExecutionException error) {
			LOGGER.error("Error occurred on publish of message to Kafka", error);
			throw new RuntimeException(error);
		}
	}
}
