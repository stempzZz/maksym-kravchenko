package com.epam.spring.time_tracking.service;

import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.dto.user.UserLoginDto;
import com.epam.spring.time_tracking.dto.user.UserRegisterDto;

public interface UserService {

    UserDto createUser(UserRegisterDto userRegisterDto);

    UserDto authUser(UserLoginDto userLoginDto);
}
