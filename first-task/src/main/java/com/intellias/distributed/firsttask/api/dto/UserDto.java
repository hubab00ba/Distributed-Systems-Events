package com.intellias.distributed.firsttask.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private String username;
    private String email;
    private Integer age;
    private String avatar;
    private String password;
    private LocalDate birthdate;
    private LocalDateTime registeredAt;
}
