package com.intellias.distributed.secondtask.api;

import com.intellias.distributed.secondtask.api.dto.UserDto;
import com.intellias.distributed.secondtask.api.mapper.UserMapper;
import com.intellias.distributed.secondtask.model.User;
import com.intellias.distributed.secondtask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Slf4j
public class UsersController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @PostMapping("create")
    public UserDto createUser(@RequestBody UserDto request) {
        log.info("Creating user with data: {}, at: {}", request, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        User document = userMapper.toDocument(request);

        User saved = userRepository.save(document);

        return userMapper.toDto(saved);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") String id) {
        log.info("Fetching user with id: {}, at: {}", id, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        Optional<User> user = userRepository.findById(id);

        return user.map(userMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("User with id: {} not found at: {}", id, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return ResponseEntity.notFound().build();
                });
    }
}
