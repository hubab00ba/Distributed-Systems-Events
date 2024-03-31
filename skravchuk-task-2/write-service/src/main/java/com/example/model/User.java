package com.example.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "user")
public class User {

    @Id
    private String id;
    private String username;
    private String email;
    private String age;
    private String avatar;
    private String password;
    private LocalDate birthdate;
    private LocalDateTime registeredAt;


    public User() {
    }

    public User(String id, String username, String email, String age, String avatar, String password, LocalDate birthdate) {
        this(username, email, age, avatar, password, birthdate);
        this.setId(id);
    }

    @PersistenceCreator
    public User(String username, String email, String age, String avatar, String password, LocalDate birthdate) {
        this.username = username;
        this.email = email;
        this.age = age;
        this.avatar = avatar;
        this.password = password;
        this.birthdate = birthdate;
        this.registeredAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}