package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.User;

import java.util.List;

public interface UserRepo {
    User getUserByEmail(String email);

    User getUserById(int userId);

    User createUser(User user);

    List<User> getUsersNotInActivity(List<User> activityUsers);
}
