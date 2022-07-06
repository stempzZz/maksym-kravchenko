package com.epam.spring.time_tracking.repository.impl;

import com.epam.spring.time_tracking.exception.ExistingException;
import com.epam.spring.time_tracking.exception.NotFoundException;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.errors.ErrorMessage;
import com.epam.spring.time_tracking.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserRepoImpl implements UserRepo {

    private final List<User> userList = new ArrayList<>();
    private int idCounter;

    @Override
    public List<User> getUsers() {
        log.info("Getting users");
        return userList;
    }

    @Override
    public List<User> getUsersNotInActivity(List<User> activityUsers) {
        log.info("Getting users who are not in activity");
        return userList.stream()
                .filter(user -> !activityUsers.contains(user))
                .filter(user -> !user.isAdmin() && !user.isBlocked())
                .collect(Collectors.toList());
    }

    @Override
    public User getUserByEmail(String email) {
        log.info("Getting user by email: {}", email);
        return userList.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    }

    @Override
    public User getUserById(long userId) {
        log.info("Getting user by id: {}", userId);
        return userList.stream()
                .filter(user -> user.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
    }

    @Override
    public User createUser(User user) {
        log.info("Creating user: {}", user);
        if (!checkForUnique(user))
            throw new ExistingException(ErrorMessage.USER_EXISTS_WITH_EMAIL);
        user.setId(++idCounter);
        userList.add(user);
        return user;
    }

    @Override
    public User blockUser(long userId, boolean isBlocked) {
        log.info("Blocking user (id={}) with value: {}", userId, isBlocked);
        User user = getUserById(userId);
        user.setBlocked(isBlocked);
        return user;
    }

    @Override
    public User updateUserInfo(long userId, User user) {
        log.info("Updating user's (id={}) information: {}", userId, user);
        User updatedUser = getUserById(userId);
        updatedUser.setLastName(user.getLastName());
        updatedUser.setFirstName(user.getFirstName());
        updatedUser.setEmail(user.getEmail());
        return updatedUser;
    }

    @Override
    public User updateUserPassword(long userId, User user) {
        log.info("Updating user's (id={}) password: {}", userId, user);
        User updatedUser = getUserById(userId);
        updatedUser.setPassword(user.getPassword());
        return updatedUser;
    }

    @Override
    public void deleteUser(long userId) {
        log.info("Deleting user with id: {}", userId);
        userList.removeIf(user -> user.getId() == userId);
    }

    @Override
    public boolean checkIfUserIsAdmin(long userId) {
        log.info("Checking is user (id={}) is admin", userId);
        return getUserById(userId).isAdmin();
    }

    private boolean checkForUnique(User user) {
        log.info("Checking user for unique");
        return userList.stream()
                .noneMatch(u -> u.getEmail().equals(user.getEmail()));
    }

}
