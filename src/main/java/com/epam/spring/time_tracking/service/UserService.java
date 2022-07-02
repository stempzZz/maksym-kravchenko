package com.epam.spring.time_tracking.service;

import com.epam.spring.time_tracking.dto.activity.ActivityForAdminProfileDto;
import com.epam.spring.time_tracking.dto.activity.ActivityForUserProfileDto;
import com.epam.spring.time_tracking.dto.user.*;

import java.util.List;

public interface UserService {

    UserDto createUser(UserInputDto userInputDto);

    UserDto authUser(UserLoginDto userLoginDto);

    List<UserInfoDto> getUsers();

    UserDto blockUser(int userId, boolean isBlocked);

    UserDto getUser(int userId);

    List<ActivityForUserProfileDto> getUserActivitiesForProfile(int userId);

    List<ActivityForAdminProfileDto> getAdminActivitiesForProfile(int userId);

    UserDto updateUserInfo(int userId, UserInputDto userInputDto);

    UserDto updateUserPassword(int userId, UserInputDto userInputDto);

    void deleteUser(int userId);
}
