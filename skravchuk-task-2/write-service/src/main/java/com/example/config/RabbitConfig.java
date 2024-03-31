package com.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

  @Autowired
  public void init(AmqpAdmin amqpAdmin) {
    amqpAdmin.declareQueue(new Queue("user-created-payload", false, false, false));
  }

  @Bean
  public MessageConverter rabbitMessageConverter() {
    ObjectMapper jsonObjectMapper = new ObjectMapper();
    jsonObjectMapper.registerModule(new JavaTimeModule());

    return new Jackson2JsonMessageConverter(jsonObjectMapper);
  }

}
