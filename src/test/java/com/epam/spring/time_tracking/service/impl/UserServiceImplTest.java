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
import com.epam.spring.time_tracking.mapper.UserMapper;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.ActivityUser;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.enums.status.ActivityStatus;
import com.epam.spring.time_tracking.model.enums.status.ActivityUserStatus;
import com.epam.spring.time_tracking.model.keys.ActivityUserKey;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.ActivityUserRepo;
import com.epam.spring.time_tracking.repository.UserRepo;
import com.epam.spring.time_tracking.test.util.ActivityDataUtilTest;
import com.epam.spring.time_tracking.test.util.UserDataUtilTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ActivityUserRepo activityUserRepo;

    @Mock
    private ActivityRepo activityRepo;

    @Test
    void getUsersTest() {
        User admin = UserDataUtilTest.getAdmin();
        User user1 = UserDataUtilTest.getUser1(0, 0.0);
        User user2 = UserDataUtilTest.getUser2(0, 0.0);

        UserDto adminDto = UserMapper.INSTANCE.toUserDtoForShowingInformation(admin);
        UserDto user1Dto = UserMapper.INSTANCE.toUserDtoForShowingInformation(user1);
        UserDto user2Dto = UserMapper.INSTANCE.toUserDtoForShowingInformation(user2);

        Page<User> users = new PageImpl<>(List.of(admin, user1, user2));
        Pageable pageable = PageRequest.of(1, 3, Sort.by("lastName", "firstName"));

        when(userRepo.findAll(pageable)).thenReturn(users);
        List<UserDto> result = userService.getUsers(pageable);

        assertThat(result, hasSize(users.getContent().size()));
        assertThat(result, hasItems(adminDto, user1Dto, user2Dto));
    }

    @Test
    void getUserTest() {
        User user = UserDataUtilTest.getUser1(0, 0.0);
        UserDto userDto = UserMapper.INSTANCE.toUserDto(user);

        when(userRepo.findById(userDto.getId())).thenReturn(Optional.of(user));
        UserDto result = userService.getUser(user.getId());

        assertThat(result, allOf(
                hasProperty("id", equalTo(userDto.getId())),
                hasProperty("lastName", equalTo(userDto.getLastName())),
                hasProperty("firstName", equalTo(userDto.getFirstName())),
                hasProperty("email", equalTo(userDto.getEmail())),
                hasProperty("password", equalTo(userDto.getPassword())),
                hasProperty("activityCount", equalTo(userDto.getActivityCount())),
                hasProperty("spentTime", equalTo(userDto.getSpentTime())),
                hasProperty("admin", equalTo(userDto.isAdmin())),
                hasProperty("blocked", equalTo(userDto.isBlocked()))
        ));
    }

    @Test
    void getUserWithNotFoundExceptionTest() {
        User user = UserDataUtilTest.getUser1(0, 0.0);

        when(userRepo.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(user.getId()));
    }

    @Test
    void createUserTest() {
        User user = UserDataUtilTest.getUser1(0, 0.0);
        UserDto userDto = UserDataUtilTest.getUser1DtoForInputData();

        when(userRepo.save(any())).thenReturn(user);
        UserDto result = userService.createUser(userDto);

        assertThat(result, allOf(
                hasProperty("id", equalTo(user.getId())),
                hasProperty("lastName", equalTo(userDto.getLastName())),
                hasProperty("firstName", equalTo(userDto.getFirstName())),
                hasProperty("activityCount", equalTo(user.getActivityCount())),
                hasProperty("spentTime", equalTo(user.getSpentTime())),
                hasProperty("admin", equalTo(userDto.isAdmin())),
                hasProperty("blocked", equalTo(userDto.isBlocked()))
        ));
    }

    @Test
    void createUserWithExistenceExceptionTest() {
        UserDto userDto = UserDataUtilTest.getUser1DtoForInputData();

        when(userRepo.existsByEmail(userDto.getEmail())).thenReturn(true);

        assertThrows(ExistenceException.class, () -> userService.createUser(userDto));
    }

    @Test
    void createUserWithVerificationExceptionTest() {
        UserDto userDto = UserDataUtilTest.getUser1DtoForInputData();
        userDto.setRepeatPassword("qwerty32123");

        assertThrows(VerificationException.class, () -> userService.createUser(userDto));
    }

    @Test
    void authUserTest() {
        User user = UserDataUtilTest.getUser1(0, 0.0);
        UserDto userDto = UserDataUtilTest.getUser1DtoForAuthorization();

        when(userRepo.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));
        UserDto result = userService.authUser(userDto);

        assertThat(result, allOf(
                hasProperty("id", equalTo(user.getId())),
                hasProperty("lastName", equalTo(user.getLastName())),
                hasProperty("firstName", equalTo(user.getFirstName())),
                hasProperty("activityCount", equalTo(user.getActivityCount())),
                hasProperty("spentTime", equalTo(user.getSpentTime())),
                hasProperty("admin", equalTo(user.isAdmin())),
                hasProperty("blocked", equalTo(user.isBlocked()))
        ));
    }

    @Test
    void authUserWithNotFoundExceptionTest() {
        UserDto userDto = UserDataUtilTest.getUser1DtoForAuthorization();

        when(userRepo.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.authUser(userDto));
    }

    @Test
    void authUserWithVerificationExceptionTest() {
        User user = UserDataUtilTest.getUser1(0, 0.0);
        UserDto userDto = UserDataUtilTest.getUser1DtoForAuthorization();
        userDto.setPassword("qwerty1231241");

        when(userRepo.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(VerificationException.class, () -> userService.authUser(userDto));
    }

    @Test
    void getUserActivitiesForProfile() {
        User user = UserDataUtilTest.getUser1(2, 0.0);
        User admin = UserDataUtilTest.getAdmin();

        Activity activity1 = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, admin);
        Activity activity2 = ActivityDataUtilTest.getActivity2(2, ActivityStatus.BY_ADMIN, admin);

        ActivityUser activity1User = new ActivityUser(new ActivityUserKey(activity1.getId(), user.getId()),
                activity1, user, null, null, 0.0, ActivityUserStatus.NOT_STARTED);
        ActivityUser activity2User = new ActivityUser(new ActivityUserKey(activity2.getId(), user.getId()),
                activity2, user, null, null, 0.0, ActivityUserStatus.NOT_STARTED);

        ActivityForUserProfileDto activity1UserDto = ActivityUserMapper.INSTANCE.toActivityForUserProfileDto(activity1User);
        ActivityForUserProfileDto activity2UserDto = ActivityUserMapper.INSTANCE.toActivityForUserProfileDto(activity2User);

        List<ActivityUser> userActivities = List.of(activity1User, activity2User);

        Pageable pageable = PageRequest.of(1, 2, Sort.by("spentTime").descending());

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(activityUserRepo.findAllByUser(user, pageable)).thenReturn(userActivities);
        List<ActivityForUserProfileDto> result = userService.getUserActivitiesForProfile(user.getId(), pageable);

        assertThat(result, hasSize(userActivities.size()));
        assertThat(result, hasItems(activity1UserDto, activity2UserDto));
    }

    @Test
    void getUserActivitiesForProfileWithNotFoundExceptionTest() {
        User user = UserDataUtilTest.getUser1(2, 0.0);

        Pageable pageable = PageRequest.of(1, 2, Sort.by("spentTime"));

        when(userRepo.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserActivitiesForProfile(user.getId(), pageable));
    }

    @Test
    void getAdminActivitiesForProfileTest() {
        User admin = UserDataUtilTest.getAdmin();

        Activity activity1 = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, admin);
        Activity activity2 = ActivityDataUtilTest.getActivity2(2, ActivityStatus.BY_ADMIN, admin);

        ActivityDto activity1Dto = ActivityMapper.INSTANCE.toActivityDtoForAdminProfile(activity1);
        ActivityDto activity2Dto = ActivityMapper.INSTANCE.toActivityDtoForAdminProfile(activity2);

        List<Activity> activities = List.of(activity1, activity2);

        Pageable pageable = PageRequest.of(1, 2, Sort.by("name"));

        when(userRepo.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(activityRepo.findAllByCreator(admin, pageable)).thenReturn(activities);
        List<ActivityDto> result = userService.getAdminActivitiesForProfile(admin.getId(), pageable);

        assertThat(result, hasSize(activities.size()));
        assertThat(result, hasItems(activity1Dto, activity2Dto));
    }

    @Test
    void getAdminActivitiesForProfileWithNotFoundExceptionTest() {
        User admin = UserDataUtilTest.getAdmin();

        Pageable pageable = PageRequest.of(1, 2, Sort.by("name"));

        when(userRepo.findById(admin.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getAdminActivitiesForProfile(admin.getId(), pageable));
    }

    @Test
    void getAdminActivitiesForProfileWithRestrictionExceptionTest() {
        User user = UserDataUtilTest.getUser1(0, 0.0);

        Pageable pageable = PageRequest.of(1, 2, Sort.by("name"));

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(RestrictionException.class, () -> userService.getAdminActivitiesForProfile(user.getId(), pageable));
    }

    @Test
    void blockUserTest() {
        User user = UserDataUtilTest.getUser1(0, 0.0);

        boolean blocked = true;

        User userAfterBlock = UserDataUtilTest.getUser1(0, 0.0);
        userAfterBlock.setBlocked(blocked);

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(userAfterBlock);
        UserDto result = userService.blockUser(user.getId(), true);

        assertThat(result, allOf(
                hasProperty("id", equalTo(user.getId())),
                hasProperty("lastName", equalTo(user.getLastName())),
                hasProperty("firstName", equalTo(user.getFirstName())),
                hasProperty("activityCount", equalTo(user.getActivityCount())),
                hasProperty("spentTime", equalTo(user.getSpentTime())),
                hasProperty("admin", equalTo(user.isAdmin())),
                hasProperty("blocked", is(blocked))
        ));
        assertThat(user.isBlocked(), is(blocked));
    }

    @Test
    void blockUserWithNotFoundExceptionTest() {
        User user = UserDataUtilTest.getUser1(0, 0.0);

        when(userRepo.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.blockUser(user.getId(), true));
    }

    @Test
    void updateUserInfoTest() {
        UserDto userDto = UserDataUtilTest.getUser1WithUpdatedInformation();
        User user = UserDataUtilTest.getUser1(0, 0.0);
        User updatedUser = UserDataUtilTest.getUser1(0, 0.0);
        updatedUser.setLastName(userDto.getLastName());
        updatedUser.setFirstName(userDto.getFirstName());
        updatedUser.setEmail(userDto.getEmail());

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepo.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userRepo.save(any())).thenReturn(updatedUser);
        UserDto result = userService.updateUserInfo(user.getId(), userDto);

        assertThat(result, allOf(
                hasProperty("id", equalTo(user.getId())),
                hasProperty("lastName", equalTo(userDto.getLastName())),
                hasProperty("firstName", equalTo(userDto.getFirstName()))
        ));
    }

    @Test
    void updateUserInfoWithNotFoundExceptionTest() {
        UserDto userDto = UserDataUtilTest.getUser1WithUpdatedInformation();

        when(userRepo.findById(userDto.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUserInfo(userDto.getId(), userDto));
    }

    @Test
    void updateUserInfoWithExistenceExceptionTest() {
        UserDto userDto = UserDataUtilTest.getUser1WithUpdatedInformation();
        User user = UserDataUtilTest.getUser1(0, 0.0);

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepo.existsByEmail(userDto.getEmail())).thenReturn(true);

        assertThrows(ExistenceException.class, () -> userService.updateUserInfo(user.getId(), userDto));
    }

    @Test
    void updateUserPasswordTest() {
        UserDto userDto = UserDataUtilTest.getUser1WithUpdatedPassword();
        User user = UserDataUtilTest.getUser1(0, 0.0);
        User updatedUser = UserMapper.INSTANCE.fromUserDto(userDto);
        updatedUser.setLastName(user.getLastName());
        updatedUser.setFirstName(user.getFirstName());

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(updatedUser);
        UserDto result = userService.updateUserPassword(user.getId(), userDto);

        verify(userRepo, times(1)).save(any());
        assertThat(result, allOf(
                hasProperty("id", equalTo(userDto.getId())),
                hasProperty("lastName", equalTo(user.getLastName())),
                hasProperty("firstName", equalTo(user.getFirstName()))
        ));
    }

    @Test
    void updateUserPasswordWithNotFoundExceptionTest() {
        UserDto userDto = UserDataUtilTest.getUser1WithUpdatedPassword();

        when(userRepo.findById(userDto.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUserPassword(userDto.getId(), userDto));
    }

    @Test
    void updateUserPasswordWithVerificationExceptionIfCurrentPasswordIsWrongTest() {
        User user = UserDataUtilTest.getUser1(0, 0.0);
        UserDto userDto = UserDataUtilTest.getUser1WithUpdatedPassword();
        userDto.setCurrentPassword("qwerty1231233");

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(VerificationException.class, () -> userService.updateUserPassword(user.getId(), userDto));
    }

    @Test
    void updateUserPasswordWithVerificationExceptionIfRepeatPasswordIsWrongTest() {
        User user = UserDataUtilTest.getUser1(0, 0.0);
        UserDto userDto = UserDataUtilTest.getUser1WithUpdatedPassword();
        userDto.setRepeatPassword("qwerty1231233");

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(VerificationException.class, () -> userService.updateUserPassword(user.getId(), userDto));
    }

    @Test
    void deleteUserTest() {
        User admin = UserDataUtilTest.getAdmin();
        User user1 = UserDataUtilTest.getUser1(2, 0.0);
        User user2 = UserDataUtilTest.getUser2(1, 0.0);

        Activity activity1 = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, admin);
        Activity activity2 = ActivityDataUtilTest.getActivity2(2, ActivityStatus.BY_ADMIN, admin);

        activity1.setUsers(
                List.of(new ActivityUser(new ActivityUserKey(activity1.getId(), user1.getId()), activity1, user1,
                        null, null, 0.0, ActivityUserStatus.NOT_STARTED))
        );
        activity2.setUsers(
                List.of(new ActivityUser(new ActivityUserKey(activity2.getId(), user1.getId()), activity2, user1,
                        null, null, 0.0, ActivityUserStatus.NOT_STARTED),
                        new ActivityUser(new ActivityUserKey(activity2.getId(), user2.getId()), activity2, user2,
                        null, null, 0.0, ActivityUserStatus.NOT_STARTED))
        );

        List<Activity> activities = List.of(activity1, activity2);

        when(userRepo.findById(admin.getId())).thenReturn(Optional.of(admin));
        when(activityRepo.findAllByCreator(admin)).thenReturn(activities);
        userService.deleteUser(admin.getId());

        verify(activityRepo, times(1)).deleteAll(activities);
        verify(userRepo, times(1)).delete(admin);
        assertThat(user1.getActivityCount(), is(0));
        assertThat(user2.getActivityCount(), is(0));
    }

    @Test
    void deleteUserWithNotFoundExceptionTest() {
        User admin = UserDataUtilTest.getAdmin();

        when(userRepo.findById(admin.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser(admin.getId()));
    }

}
