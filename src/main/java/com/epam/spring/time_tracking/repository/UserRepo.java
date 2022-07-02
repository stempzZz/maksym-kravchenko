package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.User;

import java.util.List;

public interface UserRepo {
    User getUserByEmail(String email);

    User getUserById(int userId);

    User createUser(User user);

    List<User> getUsersNotInActivity(List<User> activityUsers);

    List<User> getUsers();

    User blockUser(int userId, boolean isBlocked);

    User updateUserInfo(int userId, User user);

    User updateUserPassword(int userId, User user);

    void deleteUser(int userId);
}
