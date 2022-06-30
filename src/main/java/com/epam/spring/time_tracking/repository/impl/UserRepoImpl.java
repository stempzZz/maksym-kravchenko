package com.epam.spring.time_tracking.repository.impl;

import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.repository.UserRepo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
    public User createUser(User user) {
        user.setId(++idCounter);
        userList.add(user);
        return user;
    }
}
