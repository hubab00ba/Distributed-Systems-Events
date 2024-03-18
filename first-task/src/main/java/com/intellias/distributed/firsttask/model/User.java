package com.intellias.distributed.firsttask.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collation = "users")
@Data
public class User {
    @MongoId
    private String id;
    private String username;
    private String email;
    private Integer age;
    private String avatar;
    private String password;
    private LocalDate birthdate;
    private LocalDateTime registeredAt;
}
