package com.example.controller;

import com.example.api.message.UserDataRecord;
import com.example.model.User;
import com.example.repo.UserRepository;
import com.example.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

  private static final Logger log = LoggerFactory.getLogger(UserController.class);

  private final UserRepository userRepository;

  final RabbitTemplate rabbitTemplate;

  public UserController(UserRepository userRepository, RabbitTemplate rabbitTemplate) {
    this.userRepository = userRepository;
    this.rabbitTemplate = rabbitTemplate;
  }

  @GetMapping("/sample")
  User getUserDataSample() {
    return new User("id_1", "John", "john@email.com", "25", "<empty-hash>", "<hash>", LocalDate.now());
  }

  @PostMapping("/create")
  User createUser(@RequestBody CreateUserRequest createUserRequest) {
    User savedUser = userRepository.save(createUserEntry(createUserRequest));
    log.info("User successfully saved in DB with userId={}", savedUser.getId());
    rabbitTemplate.convertAndSend("user-created-payload", createUserDataEntry(savedUser));
    log.info("User with userId={} successfully sent to queue ", savedUser.getId());
    return savedUser;
  }

  private User createUserEntry(CreateUserRequest request) {
    return new User(request.username(), request.email(), request.age(),
      request.avatar(), request.password(), request.birthdate());
  }

  private UserDataRecord createUserDataEntry(User user) {
    return new UserDataRecord(user.getId(), user.getUsername(), user.getPassword(), user.getRegisteredAt());
  }

}
