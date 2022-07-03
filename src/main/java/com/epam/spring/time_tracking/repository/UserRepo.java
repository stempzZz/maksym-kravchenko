package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.User;

import java.util.List;

public interface UserRepo {

    List<User> getUsers();

    List<User> getUsersNotInActivity(List<User> activityUsers);

    User getUserByEmail(String email);

    User getUserById(int userId);

    User createUser(User user);

    User blockUser(int userId, boolean isBlocked);

    User updateUserInfo(int userId, User user);

    User updateUserPassword(int userId, User user);

    void deleteUser(int userId);

    boolean checkIfUserExists(int userId);

    boolean checkIfUserIsAdmin(int userId);
}
