package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.activity.ActivityForAdminProfileDto;
import com.epam.spring.time_tracking.dto.activity.ActivityForUserProfileDto;
import com.epam.spring.time_tracking.dto.group.OnAuthorization;
import com.epam.spring.time_tracking.dto.group.OnCreate;
import com.epam.spring.time_tracking.dto.group.OnUpdate;
import com.epam.spring.time_tracking.dto.group.OnUpdatePassword;
import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.dto.user.UserInfoDto;
import com.epam.spring.time_tracking.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Getting users");
        return userService.getUsers();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable int userId) {
        log.info("Getting user with id: {}", userId);
        return userService.getUser(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public UserDto createUser(@RequestBody @Validated(OnCreate.class) UserDto userDto) {
        log.info("Registering user: {}", userDto);
        return userService.createUser(userDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/auth")
    public UserDto authUser(@RequestBody @Validated(OnAuthorization.class) UserDto userDto) {
        log.info("Authorizing user: {}", userDto);
        return userService.authUser(userDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}/activity")
    public List<ActivityForUserProfileDto> getUserActivitiesForProfile(@PathVariable int userId) {
        log.info("Getting activities for user's profile, who has an id: {}", userId);
        return userService.getUserActivitiesForProfile(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin/{userId}/activity")
    public List<ActivityDto> getAdminActivitiesForProfile(@PathVariable int userId) {
        log.info("Getting activities for admin's profile, who has an id: {}", userId);
        return userService.getAdminActivitiesForProfile(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{userId}/block/{isBlocked}")
    public UserDto blockUser(@PathVariable int userId, @PathVariable boolean isBlocked) {
        log.info("Blocking user (id={}) with value: {}", userId, isBlocked);
        return userService.blockUser(userId, isBlocked);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{userId}/info")
    public UserDto updateUserInfo(@PathVariable int userId, @RequestBody @Validated(OnUpdate.class) UserDto userDto) {
        log.info("Updating user's (id={}) information: {}", userId, userDto);
        return userService.updateUserInfo(userId, userDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{userId}/password")
    public UserDto updateUserPassword(@PathVariable int userId, @RequestBody @Validated(OnUpdatePassword.class) UserDto userDto) {
        log.info("Updating user's (id={}) password: {}", userId, userDto);
        return userService.updateUserPassword(userId, userDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable int userId) {
        log.info("Deleting user with id: {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
