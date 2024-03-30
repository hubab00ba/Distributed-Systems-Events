package com.example.api.message;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public record UserDataRecord(String id, String username, String email, LocalDateTime registeredAt) {
}