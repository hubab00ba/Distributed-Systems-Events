package com.intellias.consumer;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.intellias.dto.EventEnvelope;
import com.intellias.events.handling.EventHandler;
import com.intellias.process.ProcessCondition;
import com.intellias.serialization.Deserializer;

@SuppressWarnings("unchecked")
class TestKafkaEventListener {
	public static final String EVENT_1 = "1";
	public static final String EVENT_2 = "2";
	public static final String EVENT_3 = "3";
	public static final String TYPE_X = "X";
	public static final String TYPE_Y = "Y";
	public static final String TYPE_Z = "Z";
	private static final Duration POLL_DURATION = Duration.ofSeconds(1);
	private static final String TOPIC = "my-topic";
	KafkaConsumer<String, String> kafkaConsumer = mock(KafkaConsumer.class);

	ProcessCondition processCondition = mock(ProcessCondition.class);

	Deserializer deserializer = mock(Deserializer.class);

	KafkaEventListener kafkaEventListener;

	EventHandler<Object> eventXHandler = mock(EventHandler.class);
	EventHandler<Object> eventYHandler = mock(EventHandler.class);

	ConsumerRecords<String, String> consumerRecords = mock(ConsumerRecords.class);

	@BeforeEach
	void init() {
		kafkaEventListener = new KafkaEventListener(
				Map.of(
						TOPIC, Map.of(
								TYPE_X, eventXHandler,
								TYPE_Y, eventYHandler
						)
				),
				kafkaConsumer,
				processCondition,
				POLL_DURATION,
				deserializer
		);
	}

	@Test
	void runHappyPath() {
		when(processCondition.shouldRun()).thenReturn(true, false);
		when(kafkaConsumer.poll(POLL_DURATION))
				.thenReturn(consumerRecords);
		when(consumerRecords.records(TOPIC))
				.thenReturn(List.of(
						new ConsumerRecord<>(TOPIC, 0, 0, "1", EVENT_1),
						new ConsumerRecord<>(TOPIC, 0, 100, "9", EVENT_2),
						new ConsumerRecord<>(TOPIC, 0, 5000, "24", EVENT_3)
				));
		when(deserializer.deserialize(eq(EVENT_1.getBytes(StandardCharsets.UTF_8)), eq(EventEnvelope.class)))
				.thenReturn(new EventEnvelope(TYPE_X, "1"));
		when(deserializer.deserialize(eq(EVENT_2.getBytes(StandardCharsets.UTF_8)), eq(EventEnvelope.class)))
				.thenReturn(new EventEnvelope(TYPE_Y, "2"));
		when(deserializer.deserialize(eq(EVENT_3.getBytes(StandardCharsets.UTF_8)), eq(EventEnvelope.class)))
				.thenReturn(new EventEnvelope(TYPE_Z, "3"));
		kafkaEventListener.run();
		verify(kafkaConsumer).subscribe(Set.of(TOPIC));
		verify(eventXHandler).handleEvent("1");
		verify(eventYHandler).handleEvent("2");
		verifyNoMoreInteractions(eventXHandler, eventYHandler);
	}
}

