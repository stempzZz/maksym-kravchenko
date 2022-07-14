package com.epam.spring.time_tracking.service;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.activity.ActivityForUserProfileDto;
import com.epam.spring.time_tracking.dto.user.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(Pageable pageable);

    UserDto getUser(Long userId);

    UserDto createUser(UserDto userDto);

    UserDto authUser(UserDto userDto);

    List<ActivityForUserProfileDto> getUserActivitiesForProfile(Long userId, Pageable pageable);

    List<ActivityDto> getAdminActivitiesForProfile(Long userId, Pageable pageable);

    UserDto blockUser(Long userId, boolean isBlocked);

    UserDto updateUserInfo(Long userId, UserDto userDto);

    UserDto updateUserPassword(Long userId, UserDto userDto);

    void deleteUser(Long userId);

}
