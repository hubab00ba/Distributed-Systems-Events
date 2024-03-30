package com.example.api;

import java.time.LocalDateTime;

public record CreateUserResponse(String id, String username, String email, LocalDateTime registeredAt) {

}
