package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.activity.ActivityForAdminProfileDto;
import com.epam.spring.time_tracking.dto.activity.ActivityForUserProfileDto;
import com.epam.spring.time_tracking.dto.user.*;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.UserActivity;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.UserActivityRepo;
import com.epam.spring.time_tracking.repository.UserRepo;
import com.epam.spring.time_tracking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final UserActivityRepo userActivityRepo;
    private final ActivityRepo activityRepo;
    private final ModelMapper modelMapper;

    @Override
    public UserDto createUser(UserInputDto userInputDto) {
        if (!userInputDto.getPassword().equals(userInputDto.getRepeatPassword()))
            throw new RuntimeException("password confirmation isn't success");
        User user = modelMapper.map(userInputDto, User.class);
        user = userRepo.createUser(user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto authUser(UserLoginDto userLoginDto) {
        User user = modelMapper.map(userLoginDto, User.class);
        user = userRepo.getUserByEmail(user.getEmail());
        if (!userLoginDto.getPassword().equals(user.getPassword()))
            throw new RuntimeException("wrong password was entered");
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public List<UserInfoDto> getUsers() {
        List<User> users = userRepo.getUsers();
        return users.stream()
                .map(user -> modelMapper.map(user, UserInfoDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto blockUser(int userId, boolean isBlocked) {
        User user = userRepo.blockUser(userId, isBlocked);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUser(int userId) {
        User user = userRepo.getUserById(userId);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public List<ActivityForUserProfileDto> getUserActivitiesForProfile(int userId) {
        List<UserActivity> userActivityList = userActivityRepo.getUserActivities(userId);
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
        List<Activity> activities = activityRepo.getAdminActivities(userId);
        return activities.stream()
                .map(activity -> modelMapper.map(activity, ActivityForAdminProfileDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUserInfo(int userId, UserInputDto userInputDto) {
        User user = modelMapper.map(userInputDto, User.class);
        user = userRepo.updateUserInfo(userId, user);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto updateUserPassword(int userId, UserInputDto userInputDto) {
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
        userActivityRepo.removeUserFromActivities(userId);
        userRepo.deleteUser(userId);
    }
}
