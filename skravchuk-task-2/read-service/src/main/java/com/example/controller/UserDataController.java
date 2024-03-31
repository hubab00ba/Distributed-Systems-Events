package com.example.controller;

import com.example.api.GetUserDataResponse;
import com.example.model.UserData;
import com.example.repo.UserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/user-data")
public class UserDataController {

  private static final Logger log = LoggerFactory.getLogger(UserDataController.class);

  private final UserDataRepository userDataRepository;

  public UserDataController(UserDataRepository userDataRepository) {
    this.userDataRepository = userDataRepository;
  }

  @GetMapping("/sample")
  UserData getUserDataSample() {
    return new UserData("id_1", "John", "john@email.com", LocalDateTime.now());
  }

  @GetMapping("/{userId}")
  GetUserDataResponse getUser(@PathVariable String userId) {
    //TODO: consider replacing the `GetUserDataResponse` by Spring's `ResponseEntity`
    log.info("Searching data by userId={}", userId);
    GetUserDataResponse response = userDataRepository.findById(userId)
      .map(userData -> new GetUserDataResponse("User found by id=" + userId, userData))
      .orElseGet(() -> new GetUserDataResponse("Cannot find user by id=" + userId, null));

    log.info("Result of searching data for userId={}: {}", userId, response);
    return response;
  }
}
