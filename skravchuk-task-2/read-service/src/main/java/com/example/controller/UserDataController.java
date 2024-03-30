package com.example.controller;

import com.example.api.GetUserDataResponse;
import com.example.model.UserData;
import com.example.repo.UserDataRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/user-data")
public class UserDataController {

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
    return userDataRepository.findById(userId)
      .map(userData -> new GetUserDataResponse("User found by id=" + userId, userData))
      .orElseGet(() -> new GetUserDataResponse("Cannot find user by id=" + userId, null));
  }
}
