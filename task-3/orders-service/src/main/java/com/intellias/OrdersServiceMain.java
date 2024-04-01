package com.intellias;

import static com.intellias.events.types.EventType.PREPARE_ORDER;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.gson.Gson;
import com.intellias.commands.PurchaseCommand;
import com.intellias.consumer.KafkaEventListener;
import com.intellias.dao.impl.JdbcOrdersDAO;
import com.intellias.dto.EventEnvelope;
import com.intellias.event_handler.InsufficientFundsForOrderEventHandler;
import com.intellias.event_handler.ItemForOrderNotReservedEventHandler;
import com.intellias.event_handler.ItemForOrderReservedEventHandler;
import com.intellias.event_handler.MoneyForOrderWithdrawnEventHandler;
import com.intellias.event_handler.PrepareOrderEventHandler;
import com.intellias.events.handling.impl.TypeConvertingEventHandler;
import com.intellias.events.publishing.impl.KafkaEventPublisher;
import com.intellias.events.topics.Channels;
import com.intellias.events.types.EventType;
import com.intellias.events.types.items.ItemReservationFailed;
import com.intellias.events.types.items.ItemReservationSucceeded;
import com.intellias.events.types.orders.PrepareOrder;
import com.intellias.events.types.users.InsufficientFundsForOrder;
import com.intellias.events.types.users.WithdrawMoneyForOrderSucceeded;
import com.intellias.process.impl.ThreadNotInterruptedProcessCondition;
import com.intellias.serialization.impl.GsonDeserializer;
import com.intellias.serialization.impl.GsonSerializer;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.netty.handler.codec.http.HttpResponseStatus;
import reactor.netty.DisposableServer;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.server.HttpServer;

public class OrdersServiceMain {
	public static final Duration POLL_DURATION = Duration.ofSeconds(5);
	public static final int ERROR_CODE = 1;
	public static final int HTTP_PORT = 8000;
	private static final Logger LOGGER = LoggerFactory.getLogger(OrdersServiceMain.class);

	public static void main(String[] args) {
		var properties = getKafkaProperties();

		var dataSource = new HikariDataSource(getHikariConfig());
		var jdbcTemplate = new JdbcTemplate(dataSource);
		var transactionTemplate = new TransactionTemplate(new JdbcTransactionManager(dataSource));

		var ordersDAO = new JdbcOrdersDAO(transactionTemplate, jdbcTemplate);

		var gson = new Gson();
		var serializer = new GsonSerializer(gson);
		var deserializer = new GsonDeserializer(gson);
		var kafkaEventPublisher = new KafkaEventPublisher(serializer, new KafkaProducer<>(properties));

		LOGGER.info("Starting orders service...");

		var kafkaEventListener = new KafkaEventListener(
				Map.of(
						Channels.ORDERS_SERVICE_REQUESTS_CHANNEL, Map.of(
								PREPARE_ORDER, new TypeConvertingEventHandler<>(
										deserializer,
										serializer,
										new PrepareOrderEventHandler(ordersDAO, kafkaEventPublisher),
										PrepareOrder.class
								)
						),
						Channels.ITEM_SERVICE_RESPONSES_CHANNEL, Map.of(
								EventType.ITEM_RESERVED, new TypeConvertingEventHandler<>(
										deserializer,
										serializer,
										new ItemForOrderReservedEventHandler(ordersDAO, kafkaEventPublisher),
										ItemReservationSucceeded.class
								),
								EventType.ITEM_RESERVATION_FAILED, new TypeConvertingEventHandler<>(
										deserializer,
										serializer,
										new ItemForOrderNotReservedEventHandler(ordersDAO),
										ItemReservationFailed.class
								)),
						Channels.USER_SERVICE_RESPONSES_CHANNEL, Map.of(
								EventType.WITHDRAW_MONEY_FOR_ORDER_SUCCEEDED, new TypeConvertingEventHandler<>(
										deserializer,
										serializer,
										new MoneyForOrderWithdrawnEventHandler(ordersDAO),
										WithdrawMoneyForOrderSucceeded.class
								),
								EventType.INSUFFICIENT_FUNDS_FOR_ORDER, new TypeConvertingEventHandler<>(
										deserializer,
										serializer,
										new InsufficientFundsForOrderEventHandler(ordersDAO, kafkaEventPublisher),
										InsufficientFundsForOrder.class
								)
						)
				),
				new KafkaConsumer<>(properties),
				new ThreadNotInterruptedProcessCondition(),
				POLL_DURATION,
				deserializer
		);
		var kafkaListenerThread = new Thread(kafkaEventListener);
		kafkaListenerThread.setUncaughtExceptionHandler((ignored, error) -> {
			LOGGER.error("Stopping server because of error in thread that polls Kafka topics...", error);
			System.exit(ERROR_CODE);
		});
		kafkaListenerThread.start();

		DisposableServer server =
				HttpServer.create()
						.port(HTTP_PORT)
						.noSSL()
						.protocol(HttpProtocol.HTTP11)
						.route(routes ->
								routes
										.post("/purchase", (req, rep) -> req.receive()
												.asString()
												.map(v -> v.getBytes(StandardCharsets.UTF_8))
												.map(v -> deserializer.deserialize(v, PurchaseCommand.class))
												.flatMap(v -> {
													LOGGER.info("New purchase request = {}", v);
													kafkaEventPublisher.publishEvent(
															v.orderId(),
															new EventEnvelope(
																	PREPARE_ORDER,
																	new PrepareOrder(v.orderId(), v.userId(), v.itemId())),
															Channels.ORDERS_SERVICE_REQUESTS_CHANNEL
													);
													return rep.status(HttpResponseStatus.ACCEPTED).send();
												})))
						.bindNow();

		server.onDispose().block();
	}

	private static Properties getKafkaProperties() {
		var properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaBootstrapServers());
		properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "orders-service");
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		return properties;
	}

	private static HikariConfig getHikariConfig() {
		var config = new HikariConfig();
		config.setJdbcUrl(System.getenv("JDBC_URL"));
		config.setUsername(System.getenv("DB_USERNAME"));
		config.setPassword(System.getenv("DB_PASSWORD"));
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		return config;
	}

	private static String getKafkaBootstrapServers() {
		return System.getenv("KAFKA_BOOTSTRAP_SERVERS");
	}

}
