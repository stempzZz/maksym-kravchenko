package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.activity.ActivityForUserProfileDto;
import com.epam.spring.time_tracking.dto.user.UserDto;
import com.epam.spring.time_tracking.exception.ExistenceException;
import com.epam.spring.time_tracking.exception.NotFoundException;
import com.epam.spring.time_tracking.exception.RestrictionException;
import com.epam.spring.time_tracking.exception.VerificationException;
import com.epam.spring.time_tracking.mapper.ActivityMapper;
import com.epam.spring.time_tracking.mapper.ActivityUserMapper;
import com.epam.spring.time_tracking.mapper.UpdateMapper;
import com.epam.spring.time_tracking.mapper.UserMapper;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.errors.ErrorMessage;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.ActivityUserRepo;
import com.epam.spring.time_tracking.repository.UserRepo;
import com.epam.spring.time_tracking.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final ActivityUserRepo activityUserRepo;
    private final ActivityRepo activityRepo;

    @Override
    public List<UserDto> getUsers(Pageable pageable) {
        log.info("Getting users");

        return userRepo.findAll(pageable).stream()
                .map(UserMapper.INSTANCE::toUserDtoForShowingInformation)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long userId) {
        log.info("Getting user with id: {}", userId);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
        log.info("User is received (id={}): {}", userId, user);
        return UserMapper.INSTANCE.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Creating user: {}", userDto);

        if (userRepo.existsByEmail(userDto.getEmail()))
            throw new ExistenceException(ErrorMessage.USER_EXISTS_WITH_EMAIL);
        if (!userDto.getPassword().equals(userDto.getRepeatPassword()))
            throw new VerificationException(ErrorMessage.PASSWORD_CONFIRMATION_IS_FAILED);

        User user = UserMapper.INSTANCE.fromUserDto(userDto);

        user = userRepo.save(user);
        log.info("User is created: {}", user);
        return UserMapper.INSTANCE.toUserDtoForShowingInformation(user);
    }

    @Override
    public UserDto authUser(UserDto userDto) {
        log.info("Authorizing user: {}", userDto);

        User user = userRepo.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        if (!userDto.getPassword().equals(user.getPassword()))
            throw new VerificationException(ErrorMessage.PASSWORD_VERIFICATION_IS_FAILED);

        log.info("User (id={}) is authorized", user.getId());
        return UserMapper.INSTANCE.toUserDtoForShowingInformation(user);
    }

    @Override
    public List<ActivityForUserProfileDto> getUserActivitiesForProfile(Long userId, Pageable pageable) {
        log.info("Getting activities for user's profile, who has an id: {}", userId);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        return activityUserRepo.findAllByUser(user, pageable).stream()
                .map(ActivityUserMapper.INSTANCE::toActivityForUserProfileDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityDto> getAdminActivitiesForProfile(Long userId, Pageable pageable) {
        log.info("Getting activities for admin's profile, who has an id: {}", userId);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        if (!user.isAdmin())
            throw new RestrictionException(ErrorMessage.USER_IS_NOT_AN_ADMIN);

        return activityRepo.findAllByCreator(user, pageable).stream()
                .map(ActivityMapper.INSTANCE::toActivityDtoForAdminProfile)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto blockUser(Long userId, boolean isBlocked) {
        log.info("Blocking user (id={}) with value: {}", userId, isBlocked);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
        user.setBlocked(isBlocked);

        user = userRepo.save(user);
        log.info("User (id={}) is blocked: {}", userId, user);
        return UserMapper.INSTANCE.toUserDtoForShowingInformation(user);
    }

    @Transactional
    @Override
    public UserDto updateUserInfo(Long userId, UserDto userDto) {
        log.info("Updating user's (id={}) information: {}", userId, userDto);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        if (userRepo.existsByEmail(userDto.getEmail()) && !user.getEmail().equals(userDto.getEmail()))
            throw new ExistenceException(ErrorMessage.USER_EXISTS_WITH_EMAIL);

        user = UpdateMapper.updateUserInformationWithPresentUserDtoFields(user, userDto);

        User updatedUser = userRepo.save(user);
        log.info("User's (id={}) information is updated: {}", userId, updatedUser);
        return UserMapper.INSTANCE.toUserDtoForShowingInformation(updatedUser);
    }

    @Transactional
    @Override
    public UserDto updateUserPassword(Long userId, UserDto userDto) {
        log.info("Updating user's (id={}) password: {}", userId, userDto);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        if (!userDto.getCurrentPassword().equals(user.getPassword()))
            throw new VerificationException(ErrorMessage.PASSWORD_VERIFICATION_IS_FAILED);
        if (!userDto.getPassword().equals(userDto.getRepeatPassword()))
            throw new VerificationException(ErrorMessage.PASSWORD_CONFIRMATION_IS_FAILED);

        user = UpdateMapper.updateUserPasswordWithPresentUserDtoFields(user, userDto);

        User updatedUser = userRepo.save(user);
        log.info("User's (id={}) password is updated: {}", userId, updatedUser);
        return UserMapper.INSTANCE.toUserDtoForShowingInformation(updatedUser);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        log.info("Deleting user with id: {}", userId);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        List<Activity> createdActivities = activityRepo.findAllByCreator(user);
        createdActivities.forEach(activity -> activity.getUsers()
                .forEach(activityUser -> activityUser.getUser()
                        .setActivityCount(activityUser.getUser().getActivityCount() - 1)));
        activityRepo.deleteAll(createdActivities);

        userRepo.delete(user);
        log.info("User (id={}) is deleted", userId);
    }

}
