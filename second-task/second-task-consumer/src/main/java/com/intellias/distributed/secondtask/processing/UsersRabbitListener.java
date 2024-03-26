package com.intellias.distributed.secondtask.processing;

import com.intellias.distributed.secondtask.api.dto.UserDto;
import com.intellias.distributed.secondtask.api.mapper.UserMapper;
import com.intellias.distributed.secondtask.config.RabbitConfig;
import com.intellias.distributed.secondtask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@ConditionalOnProperty(value = "enable.consuming.users", havingValue = "true")
@Slf4j
@RequiredArgsConstructor
public class UsersRabbitListener {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void consumeUser(UserDto userDto) {
        log.info("Consuming user: {} from queue: {} at: {}", userDto, RabbitConfig.QUEUE, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        userRepository.save(userMapper.toDocument(userDto));
    }
}
