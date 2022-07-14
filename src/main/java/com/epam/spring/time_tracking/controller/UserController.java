package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.api.UserApi;
import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.activity.ActivityForUserProfileDto;
import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public List<UserDto> getUsers(Pageable pageable) {
        log.info("Getting users");
        return userService.getUsers(pageable);
    }

    @Override
    public UserDto getUser(Long userId) {
        log.info("Getting user with id: {}", userId);
        return userService.getUser(userId);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Registering user: {}", userDto);
        return userService.createUser(userDto);
    }

    @Override
    public UserDto authUser(UserDto userDto) {
        log.info("Authorizing user: {}", userDto);
        return userService.authUser(userDto);
    }

    @Override
    public List<ActivityForUserProfileDto> getUserActivitiesForProfile(Long userId, Pageable pageable) {
        log.info("Getting activities for user's profile, who has an id: {}", userId);
        return userService.getUserActivitiesForProfile(userId, pageable);
    }

    @Override
    public List<ActivityDto> getAdminActivitiesForProfile(Long userId, Pageable pageable) {
        log.info("Getting activities for admin's profile, who has an id: {}", userId);
        return userService.getAdminActivitiesForProfile(userId, pageable);
    }

    @Override
    public UserDto blockUser(Long userId, boolean isBlocked) {
        log.info("Blocking user (id={}) with value: {}", userId, isBlocked);
        return userService.blockUser(userId, isBlocked);
    }

    @Override
    public UserDto updateUserInfo(Long userId, UserDto userDto) {
        log.info("Updating user's (id={}) information: {}", userId, userDto);
        return userService.updateUserInfo(userId, userDto);
    }

    @Override
    public UserDto updateUserPassword(Long userId, UserDto userDto) {
        log.info("Updating user's (id={}) password: {}", userId, userDto);
        return userService.updateUserPassword(userId, userDto);
    }

    @Override
    public ResponseEntity<Void> deleteUser(Long userId) {
        log.info("Deleting user with id: {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}
