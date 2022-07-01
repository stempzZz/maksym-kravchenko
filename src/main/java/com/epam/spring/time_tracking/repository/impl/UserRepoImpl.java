package com.epam.spring.time_tracking.repository.impl;

import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.UserActivity;
import com.epam.spring.time_tracking.repository.UserActivityRepo;
import com.epam.spring.time_tracking.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public List<User> getUsersNotInActivity(List<User> activityUsers) {
        return userList.stream()
                .filter(user -> !activityUsers.contains(user))
                .filter(user -> !user.isAdmin() && !user.isBlocked())
                .collect(Collectors.toList());
    }

    private boolean checkForUnique(User user) {
        return userList.stream()
                .noneMatch(u -> u.getEmail().equals(user.getEmail()));
    }
}
