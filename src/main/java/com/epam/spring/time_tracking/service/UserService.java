package com.epam.spring.time_tracking.service;

import com.epam.spring.time_tracking.dto.activity.ActivityForAdminProfileDto;
import com.epam.spring.time_tracking.dto.activity.ActivityForUserProfileDto;
import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.dto.user.UserInfoDto;
import com.epam.spring.time_tracking.dto.user.UserInputDto;
import com.epam.spring.time_tracking.dto.user.UserLoginDto;

import java.util.List;

public interface UserService {

    List<UserInfoDto> getUsers();

    UserDto getUser(int userId);

    UserDto createUser(UserInputDto userInputDto);

    UserDto authUser(UserLoginDto userLoginDto);

    List<ActivityForUserProfileDto> getUserActivitiesForProfile(int userId);

    List<ActivityForAdminProfileDto> getAdminActivitiesForProfile(int userId);

    UserDto blockUser(int userId, boolean isBlocked);

    UserDto updateUserInfo(int userId, UserInputDto userInputDto);

    UserDto updateUserPassword(int userId, UserInputDto userInputDto);

    void deleteUser(int userId);

}
