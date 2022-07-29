package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.category.CategoryDto;
import com.epam.spring.time_tracking.dto.user.UserInActivityDto;
import com.epam.spring.time_tracking.dto.user.UserOnlyNameDto;
import com.epam.spring.time_tracking.exception.ExistenceException;
import com.epam.spring.time_tracking.exception.NotFoundException;
import com.epam.spring.time_tracking.exception.RestrictionException;
import com.epam.spring.time_tracking.mapper.ActivityMapper;
import com.epam.spring.time_tracking.mapper.ActivityUserMapper;
import com.epam.spring.time_tracking.mapper.CategoryMapper;
import com.epam.spring.time_tracking.mapper.UserMapper;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.ActivityUser;
import com.epam.spring.time_tracking.model.Category;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.enums.status.ActivityStatus;
import com.epam.spring.time_tracking.model.enums.status.ActivityUserStatus;
import com.epam.spring.time_tracking.model.keys.ActivityUserKey;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.ActivityUserRepo;
import com.epam.spring.time_tracking.repository.UserRepo;
import com.epam.spring.time_tracking.service.CategoryService;
import com.epam.spring.time_tracking.test.util.ActivityDataUtilTest;
import com.epam.spring.time_tracking.test.util.CategoryDataUtilTest;
import com.epam.spring.time_tracking.test.util.UserDataUtilTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivityServiceImplTest {

    @InjectMocks
    private ActivityServiceImpl activityService;

    @Mock
    private ActivityRepo activityRepo;

    @Mock
    private ActivityUserRepo activityUserRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private CategoryService categoryService;

    @Test
    void getActivitiesTest() {
        User user1 = UserDataUtilTest.getUser1(1, 0);
        User user2 = UserDataUtilTest.getUser2(1, 0);

        Activity activity1 = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        Activity activity2 = ActivityDataUtilTest.getActivity2(1, ActivityStatus.BY_USER, user1);
        Activity activity3 = ActivityDataUtilTest.getActivity3(0, ActivityStatus.ADD_WAITING, user1);

        activity1.setUsers(
                List.of(new ActivityUser(new ActivityUserKey(activity1.getId(), user1.getId()), activity1, user1,
                        null, null, 0.0, ActivityUserStatus.NOT_STARTED))
        );
        activity2.setUsers(
                List.of(new ActivityUser(new ActivityUserKey(activity2.getId(), user2.getId()), activity2, user2,
                        null, null, 0.0, ActivityUserStatus.NOT_STARTED))
        );

        ActivityDto activity1Dto = ActivityMapper.INSTANCE.toActivityDto(activity1);
        ActivityDto activity2Dto = ActivityMapper.INSTANCE.toActivityDto(activity2);
        ActivityDto activity3Dto = ActivityMapper.INSTANCE.toActivityDto(activity3);

        List<Activity> activities = List.of(activity1, activity2);

        List<ActivityStatus> statuses = List.of(ActivityStatus.BY_ADMIN, ActivityStatus.BY_USER, ActivityStatus.DEL_WAITING);

        Pageable pageable = PageRequest.of(1, 3, Sort.by("createTime").descending());

        when(activityRepo.findAllByStatusIn(statuses, pageable)).thenReturn(activities);
        List<ActivityDto> result = activityService.getActivities(pageable);

        assertThat(result, hasSize(activities.size()));
        assertThat(result, hasItems(activity1Dto, activity2Dto));
        assertThat(result, not(hasItems(activity3Dto)));
    }

    @Test
    void getActivityTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());

        List<CategoryDto> categories = activity.getCategories().stream()
                .map(CategoryMapper.INSTANCE::toCategoryDto)
                .collect(Collectors.toList());

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        ActivityDto result = activityService.getActivity(activity.getId());

        assertThat(result, allOf(
                hasProperty("id", equalTo(activity.getId())),
                hasProperty("name", equalTo(activity.getName())),
                hasProperty("categories", equalTo(categories)),
                hasProperty("description", equalTo(activity.getDescription())),
                hasProperty("image", equalTo(activity.getImage())),
                hasProperty("peopleCount", equalTo(activity.getPeopleCount())),
                hasProperty("creatorId", equalTo(activity.getCreator().getId())),
                hasProperty("createTime", notNullValue()),
                hasProperty("status", equalTo(activity.getStatus()))
        ));
    }

    @Test
    void getActivityWithNotFoundExceptionTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> activityService.getActivity(activity.getId()));
    }

    @Test
    void getActivitiesForUserTest() {
        User user1 = UserDataUtilTest.getUser1(1, 0);
        User user2 = UserDataUtilTest.getUser2(1, 0);

        Activity activity1 = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        Activity activity2 = ActivityDataUtilTest.getActivity2(1, ActivityStatus.BY_USER, user2);
        Activity activity3 = ActivityDataUtilTest.getActivity3(0, ActivityStatus.ADD_WAITING, user1);

        activity1.setUsers(
                List.of(new ActivityUser(new ActivityUserKey(activity1.getId(), user1.getId()), activity1, user1,
                        null, null, 0.0, ActivityUserStatus.NOT_STARTED))
        );
        activity2.setUsers(
                List.of(new ActivityUser(new ActivityUserKey(activity2.getId(), user2.getId()), activity2, user2,
                        null, null, 0.0, ActivityUserStatus.NOT_STARTED))
        );

        ActivityDto activity1Dto = ActivityMapper.INSTANCE.toActivityDto(activity1);
        ActivityDto activity2Dto = ActivityMapper.INSTANCE.toActivityDto(activity2);
        ActivityDto activity3Dto = ActivityMapper.INSTANCE.toActivityDto(activity3);

        List<Activity> activities = List.of(activity1);

        Pageable pageable = PageRequest.of(1, 3, Sort.by("createTime").descending());

        when(userRepo.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(activityRepo.findAllForUser(user1, pageable)).thenReturn(activities);
        List<ActivityDto> result = activityService.getActivitiesForUser(user1.getId(), pageable);

        assertThat(result, hasSize(activities.size()));
        assertThat(result, hasItems(activity1Dto));
        assertThat(result, not(hasItems(activity2Dto, activity3Dto)));
    }

    @Test
    void getActivitiesForUserWithNotFoundExceptionTest() {
        User user1 = UserDataUtilTest.getUser1(1, 0);

        Pageable pageable = PageRequest.of(1, 3, Sort.by("createTime").descending());

        when(userRepo.findById(user1.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> activityService.getActivitiesForUser(user1.getId(), pageable));
    }

    @Test
    void createActivityTest() {
        User admin = UserDataUtilTest.getAdmin();

        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, admin);
        ActivityDto activityDto = ActivityDataUtilTest.getActivity1DtoForInputData(admin);

        List<Category> categories = List.of(CategoryDataUtilTest.getDefaultCategory());
        List<CategoryDto> categoryDtos = List.of(CategoryDataUtilTest.getDefaultCategoryDto());

        when(userRepo.findById(activityDto.getCreatorId())).thenReturn(Optional.of(admin));
        when(categoryService.mapCategoriesIdsToCategories(activityDto.getCategoryIds())).thenReturn(categories);
        when(activityRepo.save(any())).thenReturn(activity);
        ActivityDto result = activityService.createActivity(activityDto);

        assertThat(result, allOf(
                hasProperty("id", equalTo(activity.getId())),
                hasProperty("name", equalTo(activity.getName())),
                hasProperty("categories", equalTo(categoryDtos)),
                hasProperty("description", equalTo(activity.getDescription())),
                hasProperty("image", equalTo(activity.getImage())),
                hasProperty("peopleCount", equalTo(activity.getPeopleCount())),
                hasProperty("creatorId", equalTo(activity.getCreator().getId())),
                hasProperty("createTime", notNullValue()),
                hasProperty("status", equalTo(activity.getStatus()))
        ));
    }

    @Test
    void createActivityWithActivityNotFoundExceptionTest() {
        ActivityDto activityDto = ActivityDataUtilTest.getActivity1DtoForInputData(UserDataUtilTest.getAdmin());

        when(userRepo.findById(activityDto.getCreatorId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> activityService.createActivity(activityDto));
    }

    @Test
    void createActivityWithRestrictionExceptionTest() {
        User user = UserDataUtilTest.getUser1(0, 0.0);
        ActivityDto activityDto = ActivityDataUtilTest.getActivity1DtoForInputData(user);

        when(userRepo.findById(activityDto.getCreatorId())).thenReturn(Optional.of(user));

        assertThrows(RestrictionException.class, () -> activityService.createActivity(activityDto));
    }

    @Test
    void createActivityWithCategoryNotFoundExceptionTest() {
        User admin = UserDataUtilTest.getAdmin();
        ActivityDto activityDto = ActivityDataUtilTest.getActivity1DtoForInputData(admin);

        when(userRepo.findById(activityDto.getCreatorId())).thenReturn(Optional.of(admin));
        when(categoryService.mapCategoriesIdsToCategories(activityDto.getCategoryIds())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> activityService.createActivity(activityDto));
    }

    @Test
    void getActivityUsersTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(2, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());

        User user1 = UserDataUtilTest.getUser1(1, 0.0);
        User user2 = UserDataUtilTest.getUser2(1, 0.0);

        ActivityUser activityUser1 = new ActivityUser(new ActivityUserKey(activity.getId(), user1.getId()), activity, user1,
                null, null, 0.0, ActivityUserStatus.NOT_STARTED);
        ActivityUser activityUser2 = new ActivityUser(new ActivityUserKey(activity.getId(), user2.getId()), activity, user2,
                null, null, 0.0, ActivityUserStatus.NOT_STARTED);

        List<ActivityUser> activityUsers = List.of(activityUser1, activityUser2);

        UserInActivityDto activityUser1Dto = ActivityUserMapper.INSTANCE.toUserInActivityDto(activityUser1);
        UserInActivityDto activityUser2Dto = ActivityUserMapper.INSTANCE.toUserInActivityDto(activityUser2);

        Pageable pageable = PageRequest.of(1, 2, Sort.by("spentTime").descending());

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(activityUserRepo.findAllByActivity(activity, pageable)).thenReturn(activityUsers);
        List<UserInActivityDto> result = activityService.getActivityUsers(activity.getId(), pageable);

        assertThat(result, hasSize(activityUsers.size()));
        assertThat(result, hasItems(activityUser1Dto, activityUser2Dto));
    }

    @Test
    void getActivityUsersWithNotFoundExceptionTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(2, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());

        Pageable pageable = PageRequest.of(1, 2, Sort.by("spentTime").descending());

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> activityService.getActivityUsers(activity.getId(), pageable));
    }

    @Test
    void getUsersNotInActivityTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());

        User user1 = UserDataUtilTest.getUser1(0, 0.0);
        User user2 = UserDataUtilTest.getUser2(0, 0.0);

        List<User> users = List.of(user1, user2);

        UserOnlyNameDto user1Dto = UserMapper.INSTANCE.toUserOnlyNameDto(user1);
        UserOnlyNameDto user2Dto = UserMapper.INSTANCE.toUserOnlyNameDto(user2);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(userRepo.findAllNotInActivity(activity)).thenReturn(users);
        List<UserOnlyNameDto> result = activityService.getUsersNotInActivity(activity.getId());

        assertThat(result, hasSize(users.size()));
        assertThat(result, hasItems(user1Dto, user2Dto));
    }

    @Test
    void getUsersNotInActivityWithNotFoundExceptionTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> activityService.getUsersNotInActivity(activity.getId()));
    }

    @Test
    void getUserInActivityTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 21.4);
        ActivityUser activityUser = new ActivityUser(new ActivityUserKey(activity.getId(), user.getId()), activity, user,
                LocalDateTime.of(2022, 7, 13, 10, 31, 22),
                LocalDateTime.of(2022, 7, 13, 13, 48, 16),
                21.4, ActivityUserStatus.STOPPED);

        UserInActivityDto activityUserDto = ActivityUserMapper.INSTANCE.toUserInActivityDto(activityUser);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(activityUserRepo.findById(activityUser.getId())).thenReturn(Optional.of(activityUser));
        UserInActivityDto result = activityService.getUserInActivity(activity.getId(), user.getId());

        assertThat(result, allOf(
                hasProperty("activityId", equalTo(activityUserDto.getActivityId())),
                hasProperty("user", equalTo(activityUserDto.getUser())),
                hasProperty("startTime", equalTo(activityUserDto.getStartTime())),
                hasProperty("stopTime", equalTo(activityUserDto.getStopTime())),
                hasProperty("spentTime", equalTo(activityUserDto.getSpentTime())),
                hasProperty("status", equalTo(activityUserDto.getStatus()))
        ));
    }

    @Test
    void getUserInActivityWithActivityNotFoundExceptionTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 21.4);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> activityService.getUserInActivity(activity.getId(), user.getId()));
    }

    @Test
    void getUserInActivityWithUserNotFoundExceptionTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 21.4);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(userRepo.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> activityService.getUserInActivity(activity.getId(), user.getId()));
    }

    @Test
    void getUserInActivityWithExistenceExceptionTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 21.4);
        ActivityUserKey keyId = new ActivityUserKey(activity.getId(), user.getId());

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(activityUserRepo.findById(keyId)).thenReturn(Optional.empty());

        assertThrows(ExistenceException.class, () -> activityService.getUserInActivity(activity.getId(), user.getId()));
    }

    @Test
    void addUserToActivityTest() {
        User admin = UserDataUtilTest.getAdmin();

        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, admin);
        User user = UserDataUtilTest.getUser1(0, 0.0);

        int peopleCountAfterAdd = activity.getPeopleCount() + 1;
        int activityCountAfterAdd = user.getActivityCount() + 1;

        Activity activityAfterAdd = ActivityDataUtilTest.getActivity1(peopleCountAfterAdd, ActivityStatus.BY_ADMIN, admin);
        User userAfterAdd = UserDataUtilTest.getUser1(activityCountAfterAdd, 0.0);
        ActivityUser activityUser = new ActivityUser(new ActivityUserKey(activity.getId(), user.getId()), activityAfterAdd,
                userAfterAdd, null, null, 0.0, ActivityUserStatus.NOT_STARTED);

        UserInActivityDto activityUserDto = ActivityUserMapper.INSTANCE.toUserInActivityDto(activityUser);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(activityUserRepo.userExistsInActivity(user, activity)).thenReturn(false);
        when(activityUserRepo.save(any())).thenReturn(activityUser);
        UserInActivityDto result = activityService.addUserToActivity(activity.getId(), user.getId());

        assertThat(result, allOf(
                hasProperty("activityId", equalTo(activityUserDto.getActivityId())),
                hasProperty("user", equalTo(activityUserDto.getUser())),
                hasProperty("startTime", equalTo(activityUserDto.getStartTime())),
                hasProperty("stopTime", equalTo(activityUserDto.getStopTime())),
                hasProperty("spentTime", equalTo(activityUserDto.getSpentTime())),
                hasProperty("status", equalTo(activityUserDto.getStatus()))
        ));
        assertThat(activity.getPeopleCount(), greaterThan(0));
        assertThat(activity.getPeopleCount(), equalTo(peopleCountAfterAdd));
        assertThat(user.getActivityCount(), greaterThan(0));
        assertThat(user.getActivityCount(), equalTo(activityCountAfterAdd));
    }

    @Test
    void addUserToActivityWithActivityNotFoundExceptionTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(0, 0.0);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> activityService.addUserToActivity(activity.getId(), user.getId()));
    }

    @Test
    void addUserToActivityWithRestrictionExceptionTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.ADD_DECLINED, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(0, 0.0);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));

        assertThrows(RestrictionException.class, () -> activityService.addUserToActivity(activity.getId(), user.getId()));
    }

    @Test
    void addUserToActivityWithUserNotFoundExceptionTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(0, 0.0);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(userRepo.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> activityService.addUserToActivity(activity.getId(), user.getId()));
    }

    @Test
    void addUserToActivityWithExistenceExceptionIfUserIsAdminTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getAdmin();

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(ExistenceException.class, () -> activityService.addUserToActivity(activity.getId(), user.getId()));
    }

    @Test
    void addUserToActivityWithExistenceExceptionIfUserInActivityTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(0, 0.0);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(activityUserRepo.userExistsInActivity(user, activity)).thenReturn(true);

        assertThrows(ExistenceException.class, () -> activityService.addUserToActivity(activity.getId(), user.getId()));
    }

    @Test
    void removeUserFromActivityTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 0.0);
        ActivityUser activityUser = new ActivityUser(new ActivityUserKey(activity.getId(), user.getId()), activity,
                user, null, null, 0.0, ActivityUserStatus.NOT_STARTED);

        int peopleCountAfterRemove = activity.getPeopleCount() - 1;
        int activityCountAfterRemove = user.getActivityCount() - 1;

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(activityUserRepo.findById(activityUser.getId())).thenReturn(Optional.of(activityUser));
        activityService.removeUserFromActivity(activity.getId(), user.getId());

        verify(activityUserRepo, times(1)).delete(activityUser);
        assertThat(activity.getPeopleCount(), equalTo(peopleCountAfterRemove));
        assertThat(user.getActivityCount(), equalTo(activityCountAfterRemove));
    }

    @Test
    void removeUserFromActivityWithActivityNotFoundExceptionTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 0.0);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> activityService.removeUserFromActivity(activity.getId(), user.getId()));
    }

    @Test
    void removeUserFromActivityWithUserNotFoundExceptionTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 0.0);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(userRepo.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> activityService.removeUserFromActivity(activity.getId(), user.getId()));
    }

    @Test
    void removeUserFromActivityWithExistenceExceptionTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 0.0);
        ActivityUserKey keyId = new ActivityUserKey(activity.getId(), user.getId());

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(activityUserRepo.findById(keyId)).thenReturn(Optional.empty());

        assertThrows(ExistenceException.class, () -> activityService.removeUserFromActivity(activity.getId(), user.getId()));
    }

    @Test
    void startActivityTest() {
        LocalDateTime startTime = LocalDateTime.of(2022, 7, 25, 10, 31, 22);
        LocalDateTime stopTime = LocalDateTime.now();

        double spentTime = Duration.between(startTime, stopTime).toMillis();
        spentTime = Double.parseDouble(String.format("%.1f", spentTime / 1000.0 / 60.0 / 60.0));

        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, spentTime);
        ActivityUser activityUser = new ActivityUser(new ActivityUserKey(activity.getId(), user.getId()), activity,
                user, startTime, stopTime, spentTime, ActivityUserStatus.STOPPED);

        ActivityUser activityUserAfterStart = new ActivityUser(new ActivityUserKey(activity.getId(), user.getId()), activity,
                user, LocalDateTime.now(), null, spentTime, ActivityUserStatus.STARTED);

        UserInActivityDto activityUserAfterStartDto = ActivityUserMapper.INSTANCE.toUserInActivityDto(activityUserAfterStart);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(activityUserRepo.findById(activityUser.getId())).thenReturn(Optional.of(activityUser));
        when(activityUserRepo.save(any())).thenReturn(activityUserAfterStart);
        UserInActivityDto result = activityService.startActivity(activity.getId(), user.getId());

        assertThat(result, allOf(
                hasProperty("activityId", equalTo(activityUserAfterStartDto.getActivityId())),
                hasProperty("user", equalTo(activityUserAfterStartDto.getUser())),
                hasProperty("startTime", equalTo(activityUserAfterStartDto.getStartTime())),
                hasProperty("stopTime", equalTo(activityUserAfterStartDto.getStopTime())),
                hasProperty("spentTime", equalTo(activityUserAfterStartDto.getSpentTime())),
                hasProperty("status", equalTo(activityUserAfterStartDto.getStatus()))
        ));
        assertThat(activityUser, allOf(
                hasProperty("startTime", notNullValue()),
                hasProperty("stopTime", nullValue()),
                hasProperty("spentTime", equalTo(result.getSpentTime())),
                hasProperty("status", equalTo(ActivityUserStatus.STARTED))
        ));
        assertThat(activityUser.getStartTime(), not(equalTo(startTime)));
    }

    @Test
    void startActivityWithActivityNotFoundException() {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 0.0);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> activityService.startActivity(activity.getId(), user.getId()));
    }

    @Test
    void startActivityWithUserNotFoundException() {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 0.0);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(userRepo.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> activityService.startActivity(activity.getId(), user.getId()));
    }

    @Test
    void startActivityWithExistenceException() {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 0.0);
        ActivityUserKey keyId = new ActivityUserKey(activity.getId(), user.getId());

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(activityUserRepo.findById(keyId)).thenReturn(Optional.empty());

        assertThrows(ExistenceException.class, () -> activityService.startActivity(activity.getId(), user.getId()));
    }

    @Test
    void stopActivityTest() {
        LocalDateTime startTime = LocalDateTime.of(2022, 7, 25, 10, 31, 22);
        LocalDateTime stopTime = LocalDateTime.now();

        double spentTime = Duration.between(startTime, stopTime).toMillis();
        spentTime = Double.parseDouble(String.format("%.1f", spentTime / 1000.0 / 60.0 / 60.0));

        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(2, 13.6);
        ActivityUser activityUser = new ActivityUser(new ActivityUserKey(activity.getId(), user.getId()), activity, user,
                startTime, null, 6.2, ActivityUserStatus.STARTED);

        double spentTimeOnActivityBeforeStop = activityUser.getSpentTime();
        double spentTimeOnActivitiesBeforeStop = user.getSpentTime();
        double spentTimeOnActivityAfterStop = activityUser.getSpentTime() + spentTime;

        ActivityUser activityUserAfterStop = new ActivityUser(new ActivityUserKey(activity.getId(), user.getId()), activity,
                user, startTime, stopTime, spentTimeOnActivityAfterStop, ActivityUserStatus.STOPPED);

        UserInActivityDto activityUserAfterStopDto = ActivityUserMapper.INSTANCE.toUserInActivityDto(activityUserAfterStop);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(activityUserRepo.findById(activityUser.getId())).thenReturn(Optional.of(activityUser));
        when(activityUserRepo.save(any())).thenReturn(activityUserAfterStop);
        UserInActivityDto result = activityService.stopActivity(activity.getId(), user.getId());

        assertThat(result, allOf(
                hasProperty("activityId", equalTo(activityUserAfterStopDto.getActivityId())),
                hasProperty("user", equalTo(activityUserAfterStopDto.getUser())),
                hasProperty("startTime", equalTo(activityUserAfterStopDto.getStartTime())),
                hasProperty("stopTime", equalTo(activityUserAfterStopDto.getStopTime())),
                hasProperty("spentTime", equalTo(activityUserAfterStopDto.getSpentTime())),
                hasProperty("status", equalTo(activityUserAfterStopDto.getStatus()))
        ));
        assertThat(activityUser, allOf(
                hasProperty("startTime", notNullValue()),
                hasProperty("stopTime", notNullValue()),
                hasProperty("spentTime", equalTo(spentTimeOnActivityAfterStop)),
                hasProperty("status", equalTo(ActivityUserStatus.STOPPED))
        ));
        assertThat(activityUser.getSpentTime(), greaterThan(spentTimeOnActivityBeforeStop));
        assertThat(user.getSpentTime(), greaterThan(spentTimeOnActivitiesBeforeStop));
    }

    @Test
    void stopActivityWithActivityNotFoundException() {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(2, 13.6);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> activityService.stopActivity(activity.getId(), user.getId()));
    }

    @Test
    void stopActivityWithUserNotFoundException() {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(2, 13.6);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(userRepo.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> activityService.stopActivity(activity.getId(), user.getId()));
    }

    @Test
    void stopActivityWithExistenceException() {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(2, 13.6);
        ActivityUserKey keyId = new ActivityUserKey(activity.getId(), user.getId());

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(activityUserRepo.findById(keyId)).thenReturn(Optional.empty());

        assertThrows(ExistenceException.class, () -> activityService.stopActivity(activity.getId(), user.getId()));
    }

    @Test
    void updateActivityTest() {
        Category category1 = CategoryDataUtilTest.getCategory1();
        Category category2 = CategoryDataUtilTest.getCategory2();

        List<Category> categories = List.of(category1, category2);
        List<CategoryDto> categoryDtos = List.of(
                CategoryMapper.INSTANCE.toCategoryDto(category1),
                CategoryMapper.INSTANCE.toCategoryDto(category2));
        List<Long> categoriesIds = List.of(category1.getId(), category2.getId());

        User admin = UserDataUtilTest.getAdmin();

        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, admin);
        ActivityDto activityDto = ActivityDataUtilTest.getUpdatedActivity1Dto(categoriesIds, 0);
        Activity updatedActivity = ActivityMapper.INSTANCE.fromActivityDto(activityDto);
        updatedActivity.setId(activity.getId());
        updatedActivity.setCategories(categories);
        updatedActivity.setCreator(admin);
        updatedActivity.setStatus(ActivityStatus.BY_ADMIN);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(categoryService.mapCategoriesIdsToCategories(activityDto.getCategoryIds())).thenReturn(categories);
        when(activityRepo.save(any())).thenReturn(updatedActivity);
        ActivityDto result = activityService.updateActivity(activity.getId(), activityDto);

        assertThat(result, allOf(
                hasProperty("id", equalTo(activityDto.getId())),
                hasProperty("name", equalTo(activityDto.getName())),
                hasProperty("categories", equalTo(categoryDtos)),
                hasProperty("description", equalTo(activityDto.getDescription())),
                hasProperty("image", equalTo(activityDto.getImage())),
                hasProperty("peopleCount", equalTo(activity.getPeopleCount())),
                hasProperty("creatorId", equalTo(activity.getCreator().getId())),
                hasProperty("createTime", notNullValue()),
                hasProperty("status", equalTo(activity.getStatus()))
        ));
        assertThat(activity.getCategories(), hasItems(category1, category2));
    }

    @Test
    void updateActivityWithActivityNotFoundExceptionTest() {
        List<Long> categoriesIds = List.of(CategoryDataUtilTest.getCategory1().getId());

        User admin = UserDataUtilTest.getAdmin();

        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, admin);
        ActivityDto activityDto = ActivityDataUtilTest.getUpdatedActivity1Dto(categoriesIds, 0);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> activityService.updateActivity(activity.getId(), activityDto));
    }

    @Test
    void updateActivityWithCategoryNotFoundExceptionTest() {
        List<Long> categoriesIds = List.of(CategoryDataUtilTest.getCategory1().getId());

        User admin = UserDataUtilTest.getAdmin();

        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, admin);
        ActivityDto activityDto = ActivityDataUtilTest.getUpdatedActivity1Dto(categoriesIds, 0);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(categoryService.mapCategoriesIdsToCategories(activityDto.getCategoryIds())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> activityService.updateActivity(activity.getId(), activityDto));
    }

    @Test
    void deleteActivityTest() {
        User user1 = UserDataUtilTest.getUser1(1, 0);
        User user2 = UserDataUtilTest.getUser2(2, 0);

        Activity activity = ActivityDataUtilTest.getActivity1(2, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        activity.setUsers(
                List.of(
                        new ActivityUser(new ActivityUserKey(activity.getId(), user1.getId()), activity, user1,
                        null, null, 0.0, ActivityUserStatus.NOT_STARTED),
                        new ActivityUser(new ActivityUserKey(activity.getId(), user2.getId()), activity, user2,
                                null, null, 0.0, ActivityUserStatus.NOT_STARTED)
                )
        );

        int user1ActivityCountAfterDelete = user1.getActivityCount() - 1;
        int user2ActivityCountAfterDelete = user2.getActivityCount() - 1;

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        activityService.deleteActivity(activity.getId());

        verify(activityRepo, times(1)).delete(activity);
        assertThat(user1.getActivityCount(), equalTo(user1ActivityCountAfterDelete));
        assertThat(user2.getActivityCount(), equalTo(user2ActivityCountAfterDelete));
    }

    @Test
    void deleteActivityWithNotFoundExceptionTest() {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> activityService.deleteActivity(activity.getId()));
    }

}
