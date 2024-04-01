package com.intellias.events.publishing.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;

import com.intellias.serialization.Serializer;

@SuppressWarnings("unchecked")
class TestKafkaEventPublisher {
	private static final String KEY = "k";
	private static final Object EVENT = new Object();
	private static final String CHANNEL = "channel";
	private static final String EVENT_STR = "123";

	Serializer serializer = mock(Serializer.class);

	KafkaProducer<String, String> kafkaProducer = mock(KafkaProducer.class);

	KafkaEventPublisher kafkaEventPublisher = new KafkaEventPublisher(serializer, kafkaProducer);

	RecordMetadata recordMetadata = mock(RecordMetadata.class);

	@Test
	void publishEvent() {
		when(serializer.serialize(EVENT)).thenReturn(EVENT_STR.getBytes(StandardCharsets.UTF_8));
		when(kafkaProducer.send(new ProducerRecord<>(CHANNEL, KEY, EVENT_STR)))
				.thenReturn(CompletableFuture.completedFuture(recordMetadata));
		kafkaEventPublisher.publishEvent(KEY, EVENT, CHANNEL);
		verify(kafkaProducer).send(new ProducerRecord<>(CHANNEL, KEY, EVENT_STR));
		verify(kafkaProducer).flush();
	}
}
