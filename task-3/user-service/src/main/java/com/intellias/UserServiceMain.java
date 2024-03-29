package com.intellias;

import static com.intellias.events.topics.Channels.USER_SERVICE_REQUESTS_CHANNEL;
import static com.intellias.events.types.EventType.DEPOSIT_MONEY_FOR_ORDER;
import static com.intellias.events.types.EventType.WITHDRAW_MONEY_FOR_ORDER;

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
import com.intellias.consumer.KafkaEventListener;
import com.intellias.dao.impl.JdbcUserTransactionsDAO;
import com.intellias.dao.impl.UserBalanceDAOImpl;
import com.intellias.event_handler.DepositMoneyForOrderEventHandler;
import com.intellias.event_handler.WithdrawMoneyForOrderEventHandler;
import com.intellias.events.handling.impl.TypeConvertingEventHandler;
import com.intellias.events.publishing.impl.KafkaEventPublisher;
import com.intellias.events.types.users.DepositMoneyForOrder;
import com.intellias.events.types.users.WithdrawMoneyForOrder;
import com.intellias.process.impl.ThreadNotInterruptedProcessCondition;
import com.intellias.serialization.impl.GsonDeserializer;
import com.intellias.serialization.impl.GsonSerializer;
import com.intellias.service.impl.OrderDepositServiceImpl;
import com.intellias.service.impl.OrderPaymentServiceImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class UserServiceMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceMain.class);

	public static void main(String[] args) {
		var properties = getKafkaProperties();

		var dataSource = new HikariDataSource(getHikariConfig());
		var jdbcTemplate = new JdbcTemplate(dataSource);
		var transactionTemplate = new TransactionTemplate(new JdbcTransactionManager(dataSource));

		var userTransactionsDAO = new JdbcUserTransactionsDAO(jdbcTemplate);
		var userBalanceDAO = new UserBalanceDAOImpl(jdbcTemplate);
		var orderPaymentService = new OrderPaymentServiceImpl(
				userTransactionsDAO,
				userBalanceDAO,
				transactionTemplate
		);
		var orderDepositService = new OrderDepositServiceImpl(userTransactionsDAO, userBalanceDAO, transactionTemplate);

		var gson = new Gson();
		var serializer = new GsonSerializer(gson);
		var deserializer = new GsonDeserializer(gson);
		var kafkaEventPublisher = new KafkaEventPublisher(serializer, new KafkaProducer<>(properties));

		LOGGER.info("Starting users service...");

		new KafkaEventListener(
				Map.of(
						USER_SERVICE_REQUESTS_CHANNEL, Map.of(
								WITHDRAW_MONEY_FOR_ORDER, new TypeConvertingEventHandler<>(
										deserializer,
										serializer,
										new WithdrawMoneyForOrderEventHandler(orderPaymentService, kafkaEventPublisher),
										WithdrawMoneyForOrder.class
								),
								DEPOSIT_MONEY_FOR_ORDER, new TypeConvertingEventHandler<>(
										deserializer,
										serializer,
										new DepositMoneyForOrderEventHandler(orderDepositService, kafkaEventPublisher),
										DepositMoneyForOrder.class
								)
						)),
				new KafkaConsumer<>(properties),
				new ThreadNotInterruptedProcessCondition(),
				Duration.ofSeconds(5),
				deserializer
		).run();
	}

	private static Properties getKafkaProperties() {
		var properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaBootstrapServers());
		properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "user-service");
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
