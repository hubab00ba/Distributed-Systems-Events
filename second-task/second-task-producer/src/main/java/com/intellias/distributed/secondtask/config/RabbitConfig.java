package com.intellias.distributed.secondtask.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {
    public static final String QUEUE = "second-task-queue";

    @Bean("messageConverter")
    public MessageConverter jsonMessageConverterRabbit() {
        return new Jackson2JsonMessageConverter();
    }
}
