package com.intellias.distributed.secondtask.api.dto;

import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String username;
    private String email;
    private Integer age;
}
