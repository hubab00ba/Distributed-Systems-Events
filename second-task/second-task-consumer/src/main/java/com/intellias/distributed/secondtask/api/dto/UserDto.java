package com.intellias.distributed.secondtask.api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDto implements Serializable {
    private String id;
    private String username;
    private String email;
    private Integer age;
}
