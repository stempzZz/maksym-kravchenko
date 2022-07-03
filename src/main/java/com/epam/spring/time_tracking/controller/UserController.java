package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.dto.activity.ActivityForAdminProfileDto;
import com.epam.spring.time_tracking.dto.activity.ActivityForUserProfileDto;
import com.epam.spring.time_tracking.dto.user.*;
import com.epam.spring.time_tracking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user/register")
    public UserDto createUser(@RequestBody UserInputDto userInputDto) {
        return userService.createUser(userInputDto);
    }

    @PostMapping("/user/auth")
    public UserDto authUser(@RequestBody UserLoginDto userLoginDto) {
        return userService.authUser(userLoginDto);
    }

    @GetMapping("/user")
    public List<UserInfoDto> getUsers() {
        return userService.getUsers();
    }

    @PutMapping("/user/{userId}/block/{isBlocked}")
    public UserDto blockUser(@PathVariable int userId, @PathVariable boolean isBlocked) {
        return userService.blockUser(userId, isBlocked);
    }

    @GetMapping("/user/{userId}")
    public UserDto getUser(@PathVariable int userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/user/{userId}/activity")
    public List<ActivityForUserProfileDto> getUserActivitiesForProfile(@PathVariable int userId) {
        return userService.getUserActivitiesForProfile(userId);
    }

    @GetMapping("/admin/{userId}/activity")
    public List<ActivityForAdminProfileDto> getAdminActivitiesForProfile(@PathVariable int userId) {
        return userService.getAdminActivitiesForProfile(userId);
    }

    @PutMapping("/user/{userId}/info")
    public UserDto updateUserInfo(@PathVariable int userId, @RequestBody UserInputDto userInputDto) {
        return userService.updateUserInfo(userId, userInputDto);
    }

    @PutMapping("/user/{userId}/password")
    public UserDto updateUserPassword(@PathVariable int userId, @RequestBody UserInputDto userInputDto) {
        return userService.updateUserPassword(userId, userInputDto);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
