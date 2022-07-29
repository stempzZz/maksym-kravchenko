package com.epam.spring.time_tracking.controller;

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
import com.epam.spring.time_tracking.model.errors.ErrorMessage;
import com.epam.spring.time_tracking.model.keys.ActivityUserKey;
import com.epam.spring.time_tracking.service.UserService;
import com.epam.spring.time_tracking.test.util.ActivityDataUtilTest;
import com.epam.spring.time_tracking.test.util.UserDataUtilTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getUsersTest() throws Exception {
        UserDto adminDto = UserMapper.INSTANCE.toUserDtoForShowingInformation(UserDataUtilTest.getAdmin());
        UserDto user1Dto = UserMapper.INSTANCE.toUserDtoForShowingInformation(UserDataUtilTest.getUser1(0, 0.0));
        UserDto user2Dto = UserMapper.INSTANCE.toUserDtoForShowingInformation(UserDataUtilTest.getUser2(0, 0.0));

        List<UserDto> users = List.of(adminDto, user1Dto, user2Dto);

        when(userService.getUsers(any())).thenReturn(users);

        mockMvc.perform(get("/api/v1/user"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value(hasSize(greaterThanOrEqualTo(0))),
                        jsonPath("$").value(hasSize(lessThanOrEqualTo(10))),
                        jsonPath("$").value(hasSize(users.size())),
                        jsonPath("$[0].id").value(adminDto.getId()),
                        jsonPath("$[1].id").value(user1Dto.getId()),
                        jsonPath("$[2].id").value(user2Dto.getId())
                );
    }

    @Test
    void getUserTest() throws Exception {
        User user = UserDataUtilTest.getUser1(0, 0.0);
        UserDto userDto = UserMapper.INSTANCE.toUserDto(user);

        when(userService.getUser(user.getId())).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/user/" + user.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(user.getId()),
                        jsonPath("$.lastName").value(user.getLastName()),
                        jsonPath("$.firstName").value(user.getFirstName()),
                        jsonPath("$.email").value(user.getEmail()),
                        jsonPath("$.activityCount").value(user.getActivityCount()),
                        jsonPath("$.spentTime").value(user.getSpentTime()),
                        jsonPath("$.admin").value(user.isAdmin()),
                        jsonPath("$.blocked").value(user.isBlocked())
                );
    }

    @Test
    void getUserWithNotFoundExceptionTest() throws Exception {
        User user = UserDataUtilTest.getUser1(0, 0.0);

        when(userService.getUser(user.getId())).thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        mockMvc.perform(get("/api/v1/user/" + user.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_NOT_FOUND)
                );
    }

    @Test
    void createUserTest() throws Exception {
        User user = UserDataUtilTest.getUser1(0, 0.0);
        UserDto userDto = UserMapper.INSTANCE.toUserDtoForShowingInformation(user);
        UserDto userDtoWithInputData = UserDataUtilTest.getUser1DtoForInputData();

        ObjectMapper jsonMapper = new ObjectMapper();

        when(userService.createUser(userDtoWithInputData)).thenReturn(userDto);

        mockMvc.perform(
                        post("/api/v1/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(userDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.lastName").value(user.getLastName()),
                        jsonPath("$.firstName").value(user.getFirstName()),
                        jsonPath("$.email").doesNotExist(),
                        jsonPath("$.currentPassword").doesNotExist(),
                        jsonPath("$.password").doesNotExist(),
                        jsonPath("$.repeatPassword").doesNotExist()
                );
    }

    @Test
    void createUserWithExistenceExceptionTest() throws Exception {
        UserDto userDtoWithInputData = UserDataUtilTest.getUser1DtoForInputData();

        ObjectMapper jsonMapper = new ObjectMapper();

        when(userService.createUser(userDtoWithInputData)).thenThrow(new ExistenceException(ErrorMessage.USER_EXISTS_WITH_EMAIL));

        mockMvc.perform(
                        post("/api/v1/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(userDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_EXISTS_WITH_EMAIL)
                );
    }

    @Test
    void createUserWithVerificationExceptionTest() throws Exception {
        UserDto userDtoWithInputData = UserDataUtilTest.getUser1DtoForInputData();

        ObjectMapper jsonMapper = new ObjectMapper();

        when(userService.createUser(userDtoWithInputData)).thenThrow(new VerificationException(ErrorMessage.PASSWORD_CONFIRMATION_IS_FAILED));

        mockMvc.perform(
                        post("/api/v1/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(userDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.PASSWORD_CONFIRMATION_IS_FAILED)
                );
    }

    @Test
    void createUserWithMethodArgumentNotValidExceptionTest() throws Exception {
        UserDto userDtoWithInputData = UserDataUtilTest.getUser1DtoForInputData();
        userDtoWithInputData.setLastName("");
        userDtoWithInputData.setFirstName("");
        userDtoWithInputData.setEmail("");
        userDtoWithInputData.setCurrentPassword("");
        userDtoWithInputData.setPassword("");
        userDtoWithInputData.setRepeatPassword("");

        int validations = 6;

        ObjectMapper jsonMapper = new ObjectMapper();

        when(userService.createUser(userDtoWithInputData)).thenThrow(new VerificationException(ErrorMessage.PASSWORD_CONFIRMATION_IS_FAILED));

        mockMvc.perform(
                        post("/api/v1/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(userDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value(hasSize(validations))
                );
    }

    @Test
    void authUserTest() throws Exception {
        User user = UserDataUtilTest.getUser1(0, 0.0);
        UserDto userDto = UserMapper.INSTANCE.toUserDtoForShowingInformation(user);
        UserDto userDtoWithInputData = UserDataUtilTest.getUser1DtoForAuthorization();

        ObjectMapper jsonMapper = new ObjectMapper();

        when(userService.authUser(userDtoWithInputData)).thenReturn(userDto);

        mockMvc.perform(
                        post("/api/v1/user/auth")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(userDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(user.getId()),
                        jsonPath("$.lastName").value(user.getLastName()),
                        jsonPath("$.firstName").value(user.getFirstName()),
                        jsonPath("$.email").doesNotExist(),
                        jsonPath("$.currentPassword").doesNotExist(),
                        jsonPath("$.password").doesNotExist(),
                        jsonPath("$.repeatPassword").doesNotExist()
                );
    }

    @Test
    void authUserWithNotFoundExceptionTest() throws Exception {
        UserDto userDtoWithInputData = UserDataUtilTest.getUser1DtoForAuthorization();

        ObjectMapper jsonMapper = new ObjectMapper();

        when(userService.authUser(userDtoWithInputData)).thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        mockMvc.perform(
                        post("/api/v1/user/auth")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(userDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_NOT_FOUND)
                );
    }

    @Test
    void authUserWithVerificationExceptionTest() throws Exception {
        UserDto userDtoWithInputData = UserDataUtilTest.getUser1DtoForAuthorization();

        ObjectMapper jsonMapper = new ObjectMapper();

        when(userService.authUser(userDtoWithInputData)).thenThrow(new VerificationException(ErrorMessage.PASSWORD_VERIFICATION_IS_FAILED));

        mockMvc.perform(
                        post("/api/v1/user/auth")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(userDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.PASSWORD_VERIFICATION_IS_FAILED)
                );
    }

    @Test
    void authUserWithMethodArgumentNotValidExceptionTest() throws Exception {
        UserDto userDtoWithInputData = UserDataUtilTest.getUser1DtoForAuthorization();
        userDtoWithInputData.setEmail(null);
        userDtoWithInputData.setPassword(null);

        ObjectMapper jsonMapper = new ObjectMapper();

        mockMvc.perform(
                        post("/api/v1/user/auth")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(userDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    void getUserActivitiesForProfileTest() throws Exception {
        User user = UserDataUtilTest.getUser1(2, 0.0);
        User admin = UserDataUtilTest.getAdmin();

        Activity activity1 = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, admin);
        Activity activity2 = ActivityDataUtilTest.getActivity2(2, ActivityStatus.BY_ADMIN, admin);

        ActivityForUserProfileDto activity1UserDto = ActivityUserMapper.INSTANCE.toActivityForUserProfileDto(
                new ActivityUser(new ActivityUserKey(activity1.getId(), user.getId()),
                        activity1, user, null, null, 0.0, ActivityUserStatus.NOT_STARTED));
        ActivityForUserProfileDto activity2UserDto = ActivityUserMapper.INSTANCE.toActivityForUserProfileDto(
                new ActivityUser(new ActivityUserKey(activity2.getId(), user.getId()),
                        activity2, user, null, null, 0.0, ActivityUserStatus.NOT_STARTED));

        List<ActivityForUserProfileDto> userActivities = List.of(activity1UserDto, activity2UserDto);

        when(userService.getUserActivitiesForProfile(eq(user.getId()), any())).thenReturn(userActivities);

        mockMvc.perform(get("/api/v1/user/" + user.getId().intValue() + "/activity"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value(hasSize(greaterThanOrEqualTo(0))),
                        jsonPath("$").value(hasSize(lessThanOrEqualTo(5))),
                        jsonPath("$").value(hasSize(userActivities.size())),
                        jsonPath("$[0].id").value(activity1UserDto.getId()),
                        jsonPath("$[0].name").value(activity1UserDto.getName()),
                        jsonPath("$[0].spentTime").value(activity1UserDto.getSpentTime()),
                        jsonPath("$[0].status").value(activity1UserDto.getStatus().name()),
                        jsonPath("$[1].id").value(activity2UserDto.getId()),
                        jsonPath("$[1].name").value(activity2UserDto.getName()),
                        jsonPath("$[1].spentTime").value(activity2UserDto.getSpentTime()),
                        jsonPath("$[1].status").value(activity2UserDto.getStatus().name())
                );
    }

    @Test
    void getUserActivitiesForProfileWithNotFoundExceptionTest() throws Exception {
        User user = UserDataUtilTest.getUser1(2, 0.0);

        when(userService.getUserActivitiesForProfile(eq(user.getId()), any())).thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        mockMvc.perform(get("/api/v1/user/" + user.getId().intValue() + "/activity"))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_NOT_FOUND)
                );
    }

    @Test
    void getAdminActivitiesForProfileTest() throws Exception {
        User admin = UserDataUtilTest.getAdmin();

        ActivityDto activity1Dto = ActivityMapper.INSTANCE.toActivityDtoForAdminProfile(
                ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, admin));
        ActivityDto activity2Dto = ActivityMapper.INSTANCE.toActivityDtoForAdminProfile(
                ActivityDataUtilTest.getActivity2(2, ActivityStatus.BY_ADMIN, admin));

        List<ActivityDto> activities = List.of(activity1Dto, activity2Dto);

        when(userService.getAdminActivitiesForProfile(eq(admin.getId()), any())).thenReturn(activities);

        mockMvc.perform(get("/api/v1/user/admin/" + admin.getId().intValue() + "/activity"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value(hasSize(greaterThanOrEqualTo(0))),
                        jsonPath("$").value(hasSize(lessThanOrEqualTo(5))),
                        jsonPath("$").value(hasSize(activities.size())),
                        jsonPath("$[0].id").value(activity1Dto.getId()),
                        jsonPath("$[0].name").value(activity1Dto.getName()),
                        jsonPath("$[0].peopleCount").value(activity1Dto.getPeopleCount()),
                        jsonPath("$[1].id").value(activity2Dto.getId()),
                        jsonPath("$[1].name").value(activity2Dto.getName()),
                        jsonPath("$[1].peopleCount").value(activity2Dto.getPeopleCount())
                );
    }

    @Test
    void getAdminActivitiesForProfileWithNotFoundExceptionTest() throws Exception {
        User admin = UserDataUtilTest.getAdmin();

        when(userService.getAdminActivitiesForProfile(eq(admin.getId()), any())).thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        mockMvc.perform(get("/api/v1/user/admin/" + admin.getId().intValue() + "/activity"))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_NOT_FOUND)
                );
    }

    @Test
    void getAdminActivitiesForProfileWithRestrictionExceptionTest() throws Exception {
        User user = UserDataUtilTest.getUser1(2, 0.0);

        when(userService.getAdminActivitiesForProfile(eq(user.getId()), any())).thenThrow(new RestrictionException(ErrorMessage.USER_IS_NOT_AN_ADMIN));

        mockMvc.perform(get("/api/v1/user/admin/" + user.getId().intValue() + "/activity"))
                .andDo(print())
                .andExpectAll(
                        status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_IS_NOT_AN_ADMIN)
                );
    }

    @Test
    void blockUserTest() throws Exception {
        boolean blocked = true;

        User user = UserDataUtilTest.getUser1(0, 0.0);
        user.setBlocked(blocked);

        UserDto userDto = UserMapper.INSTANCE.toUserDtoForShowingInformation(user);

        when(userService.blockUser(user.getId(), blocked)).thenReturn(userDto);

        mockMvc.perform(put("/api/v1/user/" + user.getId() + "/block/" + blocked))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(user.getId()),
                        jsonPath("$.lastName").value(user.getLastName()),
                        jsonPath("$.firstName").value(user.getFirstName()),
                        jsonPath("$.blocked").value(user.isBlocked()),
                        jsonPath("$.email").doesNotExist(),
                        jsonPath("$.currentPassword").doesNotExist(),
                        jsonPath("$.password").doesNotExist(),
                        jsonPath("$.repeatPassword").doesNotExist()
                );
    }

    @Test
    void blockUserWithNotFoundExceptionTest() throws Exception {
        boolean blocked = true;

        User user = UserDataUtilTest.getUser1(0, 0.0);

        when(userService.blockUser(user.getId(), blocked)).thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        mockMvc.perform(put("/api/v1/user/" + user.getId() + "/block/" + blocked))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_NOT_FOUND)
                );
    }

    @Test
    void updateUserInfoTest() throws Exception {
        UserDto userDtoWithInputData = UserDataUtilTest.getUser1WithUpdatedInformation();
        User user = UserDataUtilTest.getUser1(0, 0.0);
        user.setFirstName(userDtoWithInputData.getFirstName());
        user.setLastName(userDtoWithInputData.getLastName());
        user.setEmail(userDtoWithInputData.getEmail());

        UserDto userDto = UserMapper.INSTANCE.toUserDtoForShowingInformation(user);

        ObjectMapper jsonMapper = new ObjectMapper();

        when(userService.updateUserInfo(user.getId(), userDtoWithInputData)).thenReturn(userDto);

        mockMvc.perform(
                        put("/api/v1/user/" + user.getId().intValue() + "/info")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(userDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(user.getId()),
                        jsonPath("$.lastName").value(user.getLastName()),
                        jsonPath("$.firstName").value(user.getFirstName()),
                        jsonPath("$.email").doesNotExist(),
                        jsonPath("$.currentPassword").doesNotExist(),
                        jsonPath("$.password").doesNotExist(),
                        jsonPath("$.repeatPassword").doesNotExist()
                );
    }

    @Test
    void updateUserInfoWithNotFoundExceptionTest() throws Exception {
        UserDto userDtoWithInputData = UserDataUtilTest.getUser1WithUpdatedInformation();
        User user = UserDataUtilTest.getUser1(0, 0.0);

        ObjectMapper jsonMapper = new ObjectMapper();

        when(userService.updateUserInfo(user.getId(), userDtoWithInputData)).thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        mockMvc.perform(
                        put("/api/v1/user/" + user.getId().intValue() + "/info")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(userDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_NOT_FOUND)
                );
    }

    @Test
    void updateUserInfoWithExistenceExceptionTest() throws Exception {
        UserDto userDtoWithInputData = UserDataUtilTest.getUser1WithUpdatedInformation();
        User user = UserDataUtilTest.getUser1(0, 0.0);

        ObjectMapper jsonMapper = new ObjectMapper();

        when(userService.updateUserInfo(user.getId(), userDtoWithInputData)).thenThrow(new ExistenceException(ErrorMessage.USER_EXISTS_WITH_EMAIL));

        mockMvc.perform(
                        put("/api/v1/user/" + user.getId().intValue() + "/info")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(userDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_EXISTS_WITH_EMAIL)
                );
    }

    @Test
    void updateUserInfoWithMethodArgumentNotValidExceptionTest() throws Exception {
        User user = UserDataUtilTest.getUser1(0, 0.0);
        UserDto userDtoWithInputData = UserDataUtilTest.getUser1WithUpdatedInformation();
        userDtoWithInputData.setLastName("");
        userDtoWithInputData.setFirstName("");
        userDtoWithInputData.setEmail("");
        userDtoWithInputData.setCurrentPassword("");
        userDtoWithInputData.setPassword("");
        userDtoWithInputData.setRepeatPassword("");

        int validations = 6;

        ObjectMapper jsonMapper = new ObjectMapper();

        mockMvc.perform(
                        put("/api/v1/user/" + user.getId().intValue() + "/info")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(userDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value(hasSize(validations))
                );
    }

    @Test
    void updateUserPasswordTest() throws Exception {
        UserDto userDtoWithInputData = UserDataUtilTest.getUser1WithUpdatedPassword();
        User user = UserDataUtilTest.getUser1(0, 0.0);
        user.setPassword(userDtoWithInputData.getPassword());

        UserDto userDto = UserMapper.INSTANCE.toUserDtoForShowingInformation(user);

        ObjectMapper jsonMapper = new ObjectMapper();

        when(userService.updateUserPassword(user.getId(), userDtoWithInputData)).thenReturn(userDto);

        mockMvc.perform(
                        put("/api/v1/user/" + user.getId().intValue() + "/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(userDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(user.getId()),
                        jsonPath("$.lastName").value(user.getLastName()),
                        jsonPath("$.firstName").value(user.getFirstName()),
                        jsonPath("$.email").doesNotExist(),
                        jsonPath("$.currentPassword").doesNotExist(),
                        jsonPath("$.password").doesNotExist(),
                        jsonPath("$.repeatPassword").doesNotExist()
                );
    }

    @Test
    void updateUserPasswordWithNotFoundExceptionTest() throws Exception {
        UserDto userDtoWithInputData = UserDataUtilTest.getUser1WithUpdatedPassword();
        User user = UserDataUtilTest.getUser1(0, 0.0);

        ObjectMapper jsonMapper = new ObjectMapper();

        when(userService.updateUserPassword(user.getId(), userDtoWithInputData)).thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        mockMvc.perform(
                        put("/api/v1/user/" + user.getId().intValue() + "/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(userDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_NOT_FOUND)
                );
    }

    @Test
    void updateUserPasswordWithVerificationExceptionTest() throws Exception {
        UserDto userDtoWithInputData = UserDataUtilTest.getUser1WithUpdatedPassword();
        User user = UserDataUtilTest.getUser1(0, 0.0);

        ObjectMapper jsonMapper = new ObjectMapper();

        when(userService.updateUserPassword(user.getId(), userDtoWithInputData)).thenThrow(new VerificationException(ErrorMessage.PASSWORD_VERIFICATION_IS_FAILED));

        mockMvc.perform(
                        put("/api/v1/user/" + user.getId().intValue() + "/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(userDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.PASSWORD_VERIFICATION_IS_FAILED)
                );
    }

    @Test
    void updateUserPasswordWithMethodArgumentNotValidExceptionTest() throws Exception {
        User user = UserDataUtilTest.getUser1(0, 0.0);
        UserDto userDtoWithInputData = UserDataUtilTest.getUser1WithUpdatedPassword();
        userDtoWithInputData.setLastName("");
        userDtoWithInputData.setFirstName("");
        userDtoWithInputData.setEmail("");
        userDtoWithInputData.setCurrentPassword("");
        userDtoWithInputData.setPassword("");
        userDtoWithInputData.setRepeatPassword("");

        int validations = 6;

        ObjectMapper jsonMapper = new ObjectMapper();

        mockMvc.perform(
                        put("/api/v1/user/" + user.getId().intValue() + "/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(userDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value(hasSize(validations))
                );
    }

    @Test
    void deleteUserTest() throws Exception {
        User user = UserDataUtilTest.getUser1(0, 0.0);

        doNothing().when(userService).deleteUser(user.getId());

        mockMvc.perform(delete("/api/v1/user/" + user.getId().intValue()))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(user.getId());
    }

}
