package ru.yandex.practicum.catsgram.model;

import lombok.Data;

import java.time.Instant;

@Data
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Instant registrationDate;
}