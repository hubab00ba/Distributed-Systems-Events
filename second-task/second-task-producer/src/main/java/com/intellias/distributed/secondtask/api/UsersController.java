package com.intellias.distributed.secondtask.api;

import com.intellias.distributed.secondtask.api.dto.UserDto;
import com.intellias.distributed.secondtask.api.mapper.UserMapper;
import com.intellias.distributed.secondtask.config.RabbitConfig;
import com.intellias.distributed.secondtask.model.User;
import com.intellias.distributed.secondtask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Slf4j
public class UsersController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RabbitTemplate rabbitTemplate;

    @PostMapping("create")
    public UserDto createUser(@RequestBody UserDto request) {
        log.info("Creating user with data: {}, at: {}", request, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        User document = userMapper.toDocument(request);

        User saved = userRepository.save(document);
        UserDto newUserDto = userMapper.toDto(saved);
        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE, newUserDto);

        return newUserDto;
    }
}
