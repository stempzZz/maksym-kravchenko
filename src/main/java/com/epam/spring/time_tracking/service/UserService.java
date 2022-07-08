package com.epam.spring.time_tracking.service;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.activity.ActivityForUserProfileDto;
import com.epam.spring.time_tracking.dto.user.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers();

    UserDto getUser(int userId);

    UserDto createUser(UserDto userDto);

    UserDto authUser(UserDto userDto);

    List<ActivityForUserProfileDto> getUserActivitiesForProfile(int userId);

    List<ActivityDto> getAdminActivitiesForProfile(int userId);

    UserDto blockUser(int userId, boolean isBlocked);

    UserDto updateUserInfo(int userId, UserDto userDto);

    UserDto updateUserPassword(int userId, UserDto userDto);

    void deleteUser(int userId);

}
