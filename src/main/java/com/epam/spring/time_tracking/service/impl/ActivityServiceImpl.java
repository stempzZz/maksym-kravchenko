package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.user.UserInActivityDto;
import com.epam.spring.time_tracking.dto.user.UserOnlyNameDto;
import com.epam.spring.time_tracking.exception.ExistenceException;
import com.epam.spring.time_tracking.exception.NotFoundException;
import com.epam.spring.time_tracking.exception.RestrictionException;
import com.epam.spring.time_tracking.mapper.ActivityMapper;
import com.epam.spring.time_tracking.mapper.ActivityUserMapper;
import com.epam.spring.time_tracking.mapper.UpdateMapper;
import com.epam.spring.time_tracking.mapper.UserMapper;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.ActivityUser;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.enums.status.ActivityStatus;
import com.epam.spring.time_tracking.model.enums.status.ActivityUserStatus;
import com.epam.spring.time_tracking.model.errors.ErrorMessage;
import com.epam.spring.time_tracking.model.keys.ActivityUserKey;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.ActivityUserRepo;
import com.epam.spring.time_tracking.repository.UserRepo;
import com.epam.spring.time_tracking.service.ActivityService;
import com.epam.spring.time_tracking.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepo activityRepo;
    private final ActivityUserRepo activityUserRepo;
    private final UserRepo userRepo;

    private final CategoryService categoryService;

    @Override
    public List<ActivityDto> getActivities(Pageable pageable) {
        log.info("Getting activities");

        return activityRepo.findAllByStatusIn(List.of(ActivityStatus.BY_ADMIN,
                        ActivityStatus.BY_USER, ActivityStatus.DEL_WAITING), pageable).stream()
                .map(ActivityMapper.INSTANCE::toActivityDto)
                .collect(Collectors.toList());
    }

    @Override
    public ActivityDto getActivity(Long activityId) {
        log.info("Getting activity with id: {}", activityId);

        Activity activity = activityRepo.findById(activityId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));
        log.info("Received activity by id ({}): {}", activityId, activity);
        return ActivityMapper.INSTANCE.toActivityDto(activity);
    }

    @Override
    public List<ActivityDto> getActivitiesForUser(Long userId, Pageable pageable) {
        log.info("Getting activities for user with id: {}", userId);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        return activityRepo.findAllForUser(user, pageable).stream()
                .map(ActivityMapper.INSTANCE::toActivityDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ActivityDto createActivity(ActivityDto activityDto) {
        log.info("Creating activity: {}", activityDto);

        User creator = userRepo.findById(activityDto.getCreatorId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        if (!creator.isAdmin())
            throw new RestrictionException(ErrorMessage.INSTANTLY_ACTIVITY_CREATION);

        Activity activity = ActivityMapper.INSTANCE.fromActivityDto(activityDto);
        activity.setStatus(ActivityStatus.BY_ADMIN);
        activity.setCreator(creator);
        activity.setCategories(categoryService.mapCategoriesIdsToCategories(activityDto.getCategoryIds()));
        activity = activityRepo.save(activity);
        log.info("Activity is created: {}", activity);
        return ActivityMapper.INSTANCE.toActivityDto(activity);
    }

    @Override
    public List<UserInActivityDto> getActivityUsers(Long activityId, Pageable pageable) {
        log.info("Getting users for activity with id: {}", activityId);

        Activity activity = activityRepo.findById(activityId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));

        return activityUserRepo.findAllByActivity(activity, pageable).stream()
                .map(ActivityUserMapper.INSTANCE::toUserInActivityDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserOnlyNameDto> getUsersNotInActivity(Long activityId) {
        log.info("Getting users who are not in activity with id: {}", activityId);

        Activity activity = activityRepo.findById(activityId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));

        List<User> users = userRepo.findAllNotInActivity(activity);
        return users.stream()
                .map(UserMapper.INSTANCE::toUserUserOnlyNameDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserInActivityDto getUserInActivity(Long activityId, Long userId) {
        log.info("Getting user's (id={}) information in activity with id: {}", userId, activityId);

        ActivityUser activityUser = getActivityUser(activityId, userId);
        log.info("Received user's (id={}) information in activity (id={}): {}", userId, activityId, activityUser);
        return ActivityUserMapper.INSTANCE.toUserInActivityDto(activityUser);
    }

    @Transactional
    @Override
    public UserInActivityDto addUserToActivity(Long activityId, Long userId) {
        log.info("Adding user (id={}) to an activity with id: {}", userId, activityId);

        Activity activity = activityRepo.findById(activityId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));

        if (activity.getStatus().equals(ActivityStatus.ADD_WAITING) ||
                activity.getStatus().equals(ActivityStatus.ADD_DECLINED) ||
                activity.getStatus().equals(ActivityStatus.DEL_CONFIRMED))
            throw new RestrictionException(ErrorMessage.ACTIVITY_IS_NOT_AVAILABLE);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        if (user.isAdmin())
            throw new ExistenceException(ErrorMessage.ADMIN_CAN_NOT_BE_IN_ACTIVITY);
        if (activityUserRepo.userExistsInActivity(user, activity))
            throw new ExistenceException(ErrorMessage.USER_EXISTS_IN_ACTIVITY);

        ActivityUser activityUser = new ActivityUser();

        activity.setPeopleCount(activity.getPeopleCount() + 1);
        user.setActivityCount(user.getActivityCount() + 1);

        activityUser.setActivity(activity);
        activityUser.setUser(user);
        activityUser = activityUserRepo.save(activityUser);
        log.info("User (id={}) is added to an activity (id={}): {}", userId, activityId, activityUser);
        return ActivityUserMapper.INSTANCE.toUserInActivityDto(activityUser);
    }

    @Transactional
    @Override
    public void removeUserFromActivity(Long activityId, Long userId) {
        log.info("Removing user (id={}) from an activity with id: {}", userId, activityId);

        Activity activity = activityRepo.findById(activityId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));
        ActivityUser activityUser = activityUserRepo.findById(new ActivityUserKey(activity.getId(), user.getId()))
                .orElseThrow(() -> new ExistenceException(ErrorMessage.USER_DOES_NOT_EXIST_IN_ACTIVITY));

        activityUser.getActivity().setPeopleCount(activityUser.getActivity().getPeopleCount() - 1);
        activityUser.getUser().setActivityCount(activityUser.getUser().getActivityCount() - 1);

        activityUserRepo.delete(activityUser);
        log.info("User (id={}) is removed from an activity (id={})", userId, activityId);
    }

    @Transactional
    @Override
    public UserInActivityDto startActivity(Long activityId, Long userId) {
        log.info("User (id={}) starts activity with id: {}", userId, activityId);

        ActivityUser activityUser = getActivityUser(activityId, userId);
        activityUser.setStartTime(LocalDateTime.now());
        activityUser.setStopTime(null);
        activityUser.setStatus(ActivityUserStatus.STARTED);

        activityUser = activityUserRepo.save(activityUser);
        log.info("Activity (id={}) is started by user (id={}): {}", activityId, userId, activityUser);
        return ActivityUserMapper.INSTANCE.toUserInActivityDto(activityUser);
    }

    @Transactional
    @Override
    public UserInActivityDto stopActivity(Long activityId, Long userId) {
        log.info("User (id={}) stops activity with id: {}", userId, activityId);

        ActivityUser activityUser = getActivityUser(activityId, userId);
        activityUser.setStopTime(LocalDateTime.now());

        double spentTime = Duration.between(activityUser.getStartTime(), activityUser.getStopTime()).toMillis();
        spentTime = Double.parseDouble(String.format("%.1f", spentTime / 1000.0 / 60.0 / 60.0));

        activityUser.setSpentTime(activityUser.getSpentTime() + spentTime);
        activityUser.getUser().setSpentTime(activityUser.getUser().getSpentTime() + spentTime);
        activityUser.setStatus(ActivityUserStatus.STOPPED);

        activityUser = activityUserRepo.save(activityUser);
        log.info("Activity (id={}) is stopped by user (id={}): {}", activityId, userId, activityUser);
        return ActivityUserMapper.INSTANCE.toUserInActivityDto(activityUser);
    }

    @Transactional
    @Override
    public ActivityDto updateActivity(Long activityId, ActivityDto activityDto) {
        log.info("Updating activity (id={}): {}", activityId, activityDto);

        Activity activity = activityRepo.findById(activityId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));

        List<Category> categories = categoryService.mapCategoriesIdsToCategories(activityDto.getCategoryIds());

        activity = UpdateMapper.updateActivityWithPresentActivityDtoFields(activity, activityDto);
        activity.setCategories(categories);

        activity = activityRepo.save(activity);
        log.info("Activity (id={}) is updated: {}", activityId, activity);
        return ActivityMapper.INSTANCE.toActivityDto(activity);
    }

    @Transactional
    @Override
    public void deleteActivity(Long id) {
        log.info("Deleting activity (id={})", id);
        Activity activity = activityRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));

        activity.getUsers().forEach(activityUser ->
                activityUser.getUser().setActivityCount(activityUser.getUser().getActivityCount() - 1));

        activityRepo.delete(activity);
        log.info("Activity (id={}) is deleted", id);
    }

    private ActivityUser getActivityUser(Long activityId, Long userId) {
        Activity activity = activityRepo.findById(activityId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        return activityUserRepo.findById(new ActivityUserKey(activity.getId(), user.getId()))
                .orElseThrow(() -> new ExistenceException(ErrorMessage.USER_DOES_NOT_EXIST_IN_ACTIVITY));
    }

}
