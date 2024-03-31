package com.example.api;

import java.time.LocalDate;

public record CreateUserRequest(String username, String email, String age, String avatar, String password,
                                LocalDate birthdate) {
}
