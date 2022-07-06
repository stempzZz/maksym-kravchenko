package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.User;

import java.util.List;

public interface UserRepo {

    List<User> getUsers();

    List<User> getUsersNotInActivity(List<User> activityUsers);

    User getUserByEmail(String email);

    User getUserById(long userId);

    User createUser(User user);

    User blockUser(long userId, boolean isBlocked);

    User updateUserInfo(long userId, User user);

    User updateUserPassword(long userId, User user);

    void deleteUser(long userId);

    boolean checkIfUserIsAdmin(long userId);

}
