package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.dto.user.UserLoginDto;
import com.epam.spring.time_tracking.dto.user.UserRegisterDto;
import com.epam.spring.time_tracking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users/register")
    public UserDto createUser(@RequestBody UserRegisterDto userRegisterDto) {
        return userService.createUser(userRegisterDto);
    }

    @PostMapping("/users/auth")
    public UserDto authUser(@RequestBody UserLoginDto userLoginDto) {
        return userService.authUser(userLoginDto);
    }
}
