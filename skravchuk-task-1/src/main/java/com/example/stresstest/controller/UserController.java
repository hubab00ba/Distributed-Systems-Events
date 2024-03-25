package com.example.stresstest.controller;

import com.example.stresstest.model.User;
import com.example.stresstest.repo.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/sample")
    String getUserDataSample(Model model) {
        User user = new User("id_1", "John", "john@email.com", "25", "<empty-hash>", "<hash>", LocalDate.now());

        model.addAttribute("userData", user);
        return "main_page.html";
    }

    @GetMapping("/sample/list")
    String getUserDataSampleList(Model model) {
        User user1 = new User("id_1", "John", "john@email.com", "25", "<empty-hash>", "<hash>", LocalDate.now());
        User user2 = new User("id_2", "Damien", "damien@email.com", "35", "<empty-hash>", "<hash>", LocalDate.now());

        model.addAttribute("userData", List.of(user1, user2));
        return "main_page.html";
    }

    @PostMapping("/create")
    String createUser(Model model, @RequestBody CreateUserRequest createUserRequest) {
        User saved = userRepository.save(createUserEntry(createUserRequest));
        model.addAttribute("userData", saved);
        return "main_page.html";
    }

    private User createUserEntry(CreateUserRequest request) {
        return new User(request.getUsername(), request.getEmail(), request.getAge(),
                request.getAvatar(), request.getPassword(), request.getBirthdate());
    }

}
