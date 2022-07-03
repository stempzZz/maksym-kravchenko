package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.activity.ActivityForAdminProfileDto;
import com.epam.spring.time_tracking.dto.activity.ActivityForUserProfileDto;
import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.dto.user.UserInfoDto;
import com.epam.spring.time_tracking.dto.user.UserInputDto;
import com.epam.spring.time_tracking.dto.user.UserLoginDto;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.UserActivity;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.UserActivityRepo;
import com.epam.spring.time_tracking.repository.UserRepo;
import com.epam.spring.time_tracking.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    @Override
    public List<UserInfoDto> getUsers() {
        log.info("Getting users");
        List<User> users = userRepo.getUsers();
        return users.stream()
                .map(user -> modelMapper.map(user, UserInfoDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(int userId) {
        log.info("Getting user with id: {}", userId);
        User user = userRepo.getUserById(userId);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto createUser(UserInputDto userInputDto) {
        log.info("Creating user: {}", userInputDto);
        if (!userInputDto.getPassword().equals(userInputDto.getRepeatPassword()))
            throw new RuntimeException("password confirmation isn't success");
        User user = modelMapper.map(userInputDto, User.class);
        user = userRepo.createUser(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto authUser(UserLoginDto userLoginDto) {
        log.info("Authorizing user: {}", userLoginDto);
        User user = modelMapper.map(userLoginDto, User.class);
        user = userRepo.getUserByEmail(user.getEmail());
        if (!userLoginDto.getPassword().equals(user.getPassword()))
            throw new RuntimeException("wrong password was entered");
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public List<ActivityForUserProfileDto> getUserActivitiesForProfile(int userId) {
        log.info("Getting activities for user's profile, who has an id: {}", userId);
        List<UserActivity> userActivityList = userActivityRepo.getActivitiesForUser(userId);
        return userActivityList.stream()
                .map(userActivity -> {
                    Activity activity = userActivity.getActivity();
                    ActivityForUserProfileDto activityDto = modelMapper.map(activity, ActivityForUserProfileDto.class);
                    activityDto.setSpentTime(userActivity.getSpentTime());
                    activityDto.setStatus(userActivity.getStatus());
                    return activityDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityForAdminProfileDto> getAdminActivitiesForProfile(int userId) {
        log.info("Getting activities for admin's profile, who has an id: {}", userId);
        List<Activity> activities = activityRepo.getAdminActivities(userId);
        return activities.stream()
                .map(activity -> modelMapper.map(activity, ActivityForAdminProfileDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto blockUser(int userId, boolean isBlocked) {
        log.info("Blocking user (id={}) with value: {}", userId, isBlocked);
        User user = userRepo.blockUser(userId, isBlocked);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto updateUserInfo(int userId, UserInputDto userInputDto) {
        log.info("Updating user's (id={}) information: {}", userId, userInputDto);
        User user = modelMapper.map(userInputDto, User.class);
        user = userRepo.updateUserInfo(userId, user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto updateUserPassword(int userId, UserInputDto userInputDto) {
        log.info("Updating user's (id={}) password: {}", userId, userInputDto);
        User user = userRepo.getUserById(userId);
        if (!userInputDto.getCurrentPassword().equals(user.getPassword()))
            throw new RuntimeException("wrong current password was entered");
        if (!userInputDto.getPassword().equals(userInputDto.getRepeatPassword()))
            throw new RuntimeException("password confirmation isn't success");
        User updatedUser = modelMapper.map(userInputDto, User.class);
        user = userRepo.updateUserPassword(userId, updatedUser);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public void deleteUser(int userId) {
        log.info("Deleting user with id: {}", userId);
        userActivityRepo.removeUserFromActivities(userId);
        userRepo.deleteUser(userId);
    }
}
