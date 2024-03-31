package com.example.stresstest.controller;

import java.time.LocalDate;

public class CreateUserRequest {

    private String username;
    private String email;
    private String age;
    private String avatar;
    private String password;
    private LocalDate birthdate;

    public CreateUserRequest(String username, String email, String age, String avatar, String password, LocalDate birthdate) {
        this.username = username;
        this.email = email;
        this.age = age;
        this.avatar = avatar;
        this.password = password;
        this.birthdate = birthdate;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getAge() {
        return age;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }
}
