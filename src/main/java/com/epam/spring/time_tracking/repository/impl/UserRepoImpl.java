package com.epam.spring.time_tracking.repository.impl;

import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.repository.UserRepo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepoImpl implements UserRepo {

    private final List<User> userList = new ArrayList<>();
    private int idCounter;

    @Override
    public User getUserByEmail(String email) {
        return userList.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("user is not found"));
    }

    @Override
    public User getUserById(int userId) {
        return userList.stream()
                .filter(user -> user.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("user is not found"));
    }

    @Override
    public User createUser(User user) {
        if (!checkForUnique(user))
            throw new RuntimeException("user with this email already exists");
        user.setId(++idCounter);
        userList.add(user);
        return user;
    }

    private boolean checkForUnique(User user) {
        return userList.stream()
                .noneMatch(u -> u.getEmail().equals(user.getEmail()));
    }
}
