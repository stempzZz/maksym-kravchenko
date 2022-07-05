package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.activity.ActivityForUserProfileDto;
import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.mapper.ActivityMapper;
import com.epam.spring.time_tracking.mapper.UserActivityMapper;
import com.epam.spring.time_tracking.mapper.UserMapper;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.UserActivity;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.RequestRepo;
import com.epam.spring.time_tracking.repository.UserActivityRepo;
import com.epam.spring.time_tracking.repository.UserRepo;
import com.epam.spring.time_tracking.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final UserActivityRepo userActivityRepo;
    private final ActivityRepo activityRepo;
    private final RequestRepo requestRepo;

    @Override
    public List<UserDto> getUsers() {
        log.info("Getting users");
        List<User> users = userRepo.getUsers();
        return users.stream()
                .map(UserMapper.INSTANCE::toUserDtoForShowingInformation)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(int userId) {
        log.info("Getting user with id: {}", userId);
        User user = userRepo.getUserById(userId);
        return UserMapper.INSTANCE.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Creating user: {}", userDto);
        if (!userDto.getPassword().equals(userDto.getRepeatPassword()))
            throw new RuntimeException("password confirmation isn't success");
        User user = UserMapper.INSTANCE.fromUserDto(userDto);
        user = userRepo.createUser(user);
        return UserMapper.INSTANCE.toUserDtoForShowingInformation(user);
    }

    @Override
    public UserDto authUser(UserDto userDto) {
        log.info("Authorizing user: {}", userDto);
        User user = UserMapper.INSTANCE.fromUserDto(userDto);
        user = userRepo.getUserByEmail(user.getEmail());
        if (!userDto.getPassword().equals(user.getPassword()))
            throw new RuntimeException("wrong password was entered");
        return UserMapper.INSTANCE.toUserDtoForShowingInformation(user);
    }

    @Override
    public List<ActivityForUserProfileDto> getUserActivitiesForProfile(int userId) {
        log.info("Getting activities for user's profile, who has an id: {}", userId);
        List<UserActivity> userActivityList = userActivityRepo.getActivitiesForUser(userId);
        return userActivityList.stream()
                .map(UserActivityMapper.INSTANCE::toActivityForUserProfileDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityDto> getAdminActivitiesForProfile(int userId) {
        log.info("Getting activities for admin's profile, who has an id: {}", userId);
        List<Activity> activities = activityRepo.getActivitiesCreatedByUser(userId, true);
        return activities.stream()
                .map(ActivityMapper.INSTANCE::toActivityDtoForAdminProfile)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto blockUser(int userId, boolean isBlocked) {
        log.info("Blocking user (id={}) with value: {}", userId, isBlocked);
        User user = userRepo.blockUser(userId, isBlocked);
        return UserMapper.INSTANCE.toUserDtoForShowingInformation(user);
    }

    @Override
    public UserDto updateUserInfo(int userId, UserDto userDto) {
        log.info("Updating user's (id={}) information: {}", userId, userDto);
        User user = UserMapper.INSTANCE.fromUserDto(userDto);
        user = userRepo.updateUserInfo(userId, user);
        return UserMapper.INSTANCE.toUserDtoForShowingInformation(user);
    }

    @Override
    public UserDto updateUserPassword(int userId, UserDto userDto) {
        log.info("Updating user's (id={}) password: {}", userId, userDto);
        User user = userRepo.getUserById(userId);
        if (!userDto.getCurrentPassword().equals(user.getPassword()))
            throw new RuntimeException("wrong current password was entered");
        if (!userDto.getPassword().equals(userDto.getRepeatPassword()))
            throw new RuntimeException("password confirmation isn't success");
        User updatedUser = UserMapper.INSTANCE.fromUserDto(userDto);
        user = userRepo.updateUserPassword(userId, updatedUser);
        return UserMapper.INSTANCE.toUserDtoForShowingInformation(user);
    }

    @Override
    public void deleteUser(int userId) {
        log.info("Deleting user with id: {}", userId);
        activityRepo.getActivitiesCreatedByUser(userId, false).forEach(activity -> {
            userActivityRepo.deleteActivity(activity.getId());
            requestRepo.deleteRequestsWithActivity(activity.getId());
            activityRepo.deleteActivityById(activity.getId());
        });
        userActivityRepo.removeUserFromActivities(userId);
        userRepo.deleteUser(userId);
    }
}
