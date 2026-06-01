package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        if (isDuplicateEmail(null, user.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User existingUser = users.get(user.getId());
        if (existingUser == null) {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }

        // Проверяем дубликат email только если его пытаются изменить
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (isDuplicateEmail(user.getId(), user.getEmail())) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
        }

        // Обновляем только те поля, которые не null
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getPassword() != null) {
            existingUser.setPassword(user.getPassword());
        }

        return existingUser;
    }

    /**
     * Проверяет, используется ли email другим пользователем.
     * @param excludeId ID пользователя, которого нужно исключить из проверки (для обновления)
     * @param email проверяемый email
     */
    private boolean isDuplicateEmail(Long excludeId, String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return users.values().stream()
                .filter(u -> !u.getId().equals(excludeId)) // исключаем текущего пользователя
                .anyMatch(u -> email.equals(u.getEmail()));
    }

    private long getNextId() {
        return users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0) + 1;
    }
}