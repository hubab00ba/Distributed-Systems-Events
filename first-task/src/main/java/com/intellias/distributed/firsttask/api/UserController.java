package com.intellias.distributed.firsttask.api;

import com.intellias.distributed.firsttask.api.dto.UserDto;
import com.intellias.distributed.firsttask.api.mappers.UserMapper;
import com.intellias.distributed.firsttask.model.User;
import com.intellias.distributed.firsttask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("user")
public class UserController {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @GetMapping(value = "get")
    @ResponseBody
    public UserDto getUser() {
        log.info("Preparing static user data at {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return UserDto.builder()
                .username("static-user")
                .age(27)
                .email("email@example.com")
                .avatar(RandomStringUtils.randomAlphabetic(100))
                .password(RandomStringUtils.random(16))
                .birthdate(LocalDate.of(1984, 12, 24))
                .registeredAt(LocalDateTime.now())
                .build();
    }

    @PostMapping(value = "create")
    public UserDto createUser(@RequestBody UserDto request) {
        log.info("Creating new user from create request at {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        User document = userMapper.toDocument(request);
        document.setId(UUID.randomUUID().toString());

        User saved = userRepository.save(document);

        return userMapper.toDto(saved);
    }
}
