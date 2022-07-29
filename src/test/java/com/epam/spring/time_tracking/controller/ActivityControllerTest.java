package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
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
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.enums.status.ActivityStatus;
import com.epam.spring.time_tracking.model.enums.status.ActivityUserStatus;
import com.epam.spring.time_tracking.model.errors.ErrorMessage;
import com.epam.spring.time_tracking.model.keys.ActivityUserKey;
import com.epam.spring.time_tracking.service.ActivityService;
import com.epam.spring.time_tracking.test.util.ActivityDataUtilTest;
import com.epam.spring.time_tracking.test.util.CategoryDataUtilTest;
import com.epam.spring.time_tracking.test.util.UserDataUtilTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ActivityController.class)
public class ActivityControllerTest {

    @MockBean
    private ActivityService activityService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getActivitiesTest() throws Exception {
        User user1 = UserDataUtilTest.getUser1(1, 0);

        Activity activity1 = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        Activity activity2 = ActivityDataUtilTest.getActivity2(1, ActivityStatus.BY_USER, user1);

        ActivityDto activity1Dto = ActivityMapper.INSTANCE.toActivityDto(activity1);
        ActivityDto activity2Dto = ActivityMapper.INSTANCE.toActivityDto(activity2);

        List<ActivityDto> activities = List.of(activity1Dto, activity2Dto);

        when(activityService.getActivities(any())).thenReturn(activities);

        mockMvc.perform(get("/api/v1/activity"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value(hasSize(greaterThanOrEqualTo(0))),
                        jsonPath("$").value(hasSize(lessThanOrEqualTo(6))),
                        jsonPath("$").value(hasSize(activities.size())),
                        jsonPath("$[0].id").value(activity1Dto.getId()),
                        jsonPath("$[0].status").value(
                                anyOf(
                                        equalTo(ActivityStatus.BY_ADMIN.name()),
                                        equalTo(ActivityStatus.BY_USER.name()),
                                        equalTo(ActivityStatus.DEL_WAITING.name())
                                )
                        ),
                        jsonPath("$[1].id").value(activity2Dto.getId()),
                        jsonPath("$[1].status").value(
                                anyOf(
                                        equalTo(ActivityStatus.BY_ADMIN.name()),
                                        equalTo(ActivityStatus.BY_USER.name()),
                                        equalTo(ActivityStatus.DEL_WAITING.name())
                                )
                        )
                );
    }

    @Test
    void getActivityTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        ActivityDto activityDto = ActivityMapper.INSTANCE.toActivityDto(activity);

        String createTime = activity.getCreateTime().toString().substring(0, activity.getCreateTime().toString().length() - 2);

        when(activityService.getActivity(activity.getId())).thenReturn(activityDto);

        mockMvc.perform(get("/api/v1/activity/" + activity.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(activity.getId()),
                        jsonPath("$.name").value(activity.getName()),
                        jsonPath("$.categories[0].id").value(activity.getCategories().get(0).getId()),
                        jsonPath("$.description").value(activity.getDescription()),
                        jsonPath("$.image").value(activity.getImage()),
                        jsonPath("$.peopleCount").value(activity.getPeopleCount()),
                        jsonPath("$.creatorId").value(activity.getCreator().getId()),
                        jsonPath("$.createTime").value(createTime),
                        jsonPath("$.status").value(activity.getStatus().name())
                );
    }

    @Test
    void getActivityWithNotFoundExceptionTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());

        when(activityService.getActivity(activity.getId())).thenThrow(new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));

        mockMvc.perform(get("/api/v1/activity/" + activity.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.ACTIVITY_NOT_FOUND)
                );
    }

    @Test
    void getActivitiesForUserTest() throws Exception {
        User user = UserDataUtilTest.getUser1(1, 0);

        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        ActivityDto activityDto = ActivityMapper.INSTANCE.toActivityDto(activity);

        List<ActivityDto> activities = List.of(activityDto);

        when(activityService.getActivitiesForUser(eq(user.getId()), any())).thenReturn(activities);

        mockMvc.perform(get("/api/v1/activity/user/" + user.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value(hasSize(greaterThanOrEqualTo(0))),
                        jsonPath("$").value(hasSize(lessThanOrEqualTo(6))),
                        jsonPath("$").value(hasSize(activities.size())),
                        jsonPath("$[0].id").value(activityDto.getId().intValue()),
                        jsonPath("$[0].status").value(
                                anyOf(
                                        equalTo(ActivityStatus.BY_ADMIN.name()),
                                        equalTo(ActivityStatus.BY_USER.name()),
                                        equalTo(ActivityStatus.DEL_WAITING.name())
                                )
                        )
                );
    }

    @Test
    void getActivitiesForUserWithNotFoundExceptionTest() throws Exception {
        User user = UserDataUtilTest.getUser1(1, 0);

        when(activityService.getActivitiesForUser(eq(user.getId()), any())).thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        mockMvc.perform(get("/api/v1/activity/user/" + user.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_NOT_FOUND)
                );
    }

    @Test
    void createActivityTest() throws Exception {
        User admin = UserDataUtilTest.getAdmin();

        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, admin);
        ActivityDto activityDto = ActivityMapper.INSTANCE.toActivityDto(activity);
        ActivityDto activityDtoWithInputData = ActivityDataUtilTest.getActivity1DtoForInputData(admin);

        ObjectMapper jsonMapper = new ObjectMapper();

        when(activityService.createActivity(activityDtoWithInputData)).thenReturn(activityDto);

        mockMvc.perform(
                        post("/api/v1/activity")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(activityDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.name").value(activity.getName()),
                        jsonPath("$.categories").value(hasSize(greaterThanOrEqualTo(1))),
                        jsonPath("$.description").value(activity.getDescription()),
                        jsonPath("$.image").value(activity.getImage())
                );
    }

    @Test
    void createActivityWithNotFoundExceptionTest() throws Exception {
        ActivityDto activityDtoWithInputData = ActivityDataUtilTest.getActivity1DtoForInputData(UserDataUtilTest.getAdmin());

        ObjectMapper jsonMapper = new ObjectMapper();

        when(activityService.createActivity(activityDtoWithInputData)).thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        mockMvc.perform(
                        post("/api/v1/activity")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(activityDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_NOT_FOUND)
                );
    }

    @Test
    void createActivityWithRestrictionExceptionTest() throws Exception {
        ActivityDto activityDtoWithInputData = ActivityDataUtilTest.getActivity1DtoForInputData(UserDataUtilTest.getAdmin());

        ObjectMapper jsonMapper = new ObjectMapper();

        when(activityService.createActivity(activityDtoWithInputData)).thenThrow(new RestrictionException(ErrorMessage.INSTANTLY_ACTIVITY_CREATION));

        mockMvc.perform(
                        post("/api/v1/activity")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(activityDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.INSTANTLY_ACTIVITY_CREATION)
                );
    }

    @Test
    void createActivityWithMethodArgumentNotValidExceptionTest() throws Exception {
        ActivityDto activityDtoWithInputData = ActivityDataUtilTest.getActivity1DtoForInputData(UserDataUtilTest.getAdmin());
        activityDtoWithInputData.setName("");
        activityDtoWithInputData.setCategories(List.of(CategoryMapper.INSTANCE.toCategoryDto(CategoryDataUtilTest.getCategory1())));
        activityDtoWithInputData.setDescription("");
        activityDtoWithInputData.setCreatorId(null);
        activityDtoWithInputData.setCreateTime(LocalDateTime.now());
        activityDtoWithInputData.setStatus(ActivityStatus.BY_ADMIN);

        int validations = 6;

        ObjectMapper jsonMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        mockMvc.perform(
                        post("/api/v1/activity")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(activityDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value(hasSize(validations))
                );
    }

    @Test
    void getActivityUsersTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(2, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());

        User user1 = UserDataUtilTest.getUser1(1, 0.0);
        User user2 = UserDataUtilTest.getUser2(1, 0.0);

        ActivityUser activityUser1 = new ActivityUser(new ActivityUserKey(activity.getId(), user1.getId()), activity, user1,
                null, null, 0.0, ActivityUserStatus.NOT_STARTED);
        ActivityUser activityUser2 = new ActivityUser(new ActivityUserKey(activity.getId(), user2.getId()), activity, user2,
                null, null, 0.0, ActivityUserStatus.NOT_STARTED);

        List<UserInActivityDto> activityUsers = List.of(
                ActivityUserMapper.INSTANCE.toUserInActivityDto(activityUser1),
                ActivityUserMapper.INSTANCE.toUserInActivityDto(activityUser2)
        );

        when(activityService.getActivityUsers(eq(activity.getId()), any())).thenReturn(activityUsers);

        mockMvc.perform(get("/api/v1/activity/" + activity.getId().intValue() + "/user"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value(hasSize(greaterThanOrEqualTo(0))),
                        jsonPath("$").value(hasSize(lessThanOrEqualTo(5))),
                        jsonPath("$").value(hasSize(activityUsers.size())),
                        jsonPath("$[0].activityId").value(activityUser1.getActivity().getId()),
                        jsonPath("$[0].user.id").value(activityUser1.getUser().getId()),
                        jsonPath("$[1].activityId").value(activityUser2.getActivity().getId()),
                        jsonPath("$[1].user.id").value(activityUser2.getUser().getId())
                );
    }

    @Test
    void getActivityUsersWithNotFoundExceptionTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(2, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());

        when(activityService.getActivityUsers(eq(activity.getId()), any())).thenThrow(new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));

        mockMvc.perform(get("/api/v1/activity/" + activity.getId().intValue() + "/user"))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.ACTIVITY_NOT_FOUND)
                );
    }

    @Test
    void getUsersNotInActivityTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());

        User user1 = UserDataUtilTest.getUser1(0, 0.0);
        User user2 = UserDataUtilTest.getUser2(0, 0.0);

        List<UserOnlyNameDto> users = List.of(
                UserMapper.INSTANCE.toUserOnlyNameDto(user1),
                UserMapper.INSTANCE.toUserOnlyNameDto(user2)
        );

        when(activityService.getUsersNotInActivity(activity.getId())).thenReturn(users);

        mockMvc.perform(get("/api/v1/activity/" + activity.getId().intValue() + "/user/available"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value(hasSize(users.size())),
                        jsonPath("$[0].id").value(user1.getId()),
                        jsonPath("$[1].id").value(user2.getId())
                );
    }

    @Test
    void getUsersNotInActivityWithNotFoundExceptionTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());

        when(activityService.getUsersNotInActivity(activity.getId())).thenThrow(new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));

        mockMvc.perform(get("/api/v1/activity/" + activity.getId().intValue() + "/user/available"))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.ACTIVITY_NOT_FOUND)
                );
    }

    @Test
    void getUserInActivityTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 21.4);
        ActivityUser activityUser = new ActivityUser(new ActivityUserKey(activity.getId(), user.getId()), activity, user,
                LocalDateTime.of(2022, 7, 13, 10, 31, 22),
                LocalDateTime.of(2022, 7, 13, 13, 48, 16),
                21.4, ActivityUserStatus.STOPPED);

        UserInActivityDto activityUserDto = ActivityUserMapper.INSTANCE.toUserInActivityDto(activityUser);

        when(activityService.getUserInActivity(activity.getId(), user.getId())).thenReturn(activityUserDto);

        mockMvc.perform(get("/api/v1/activity/" + activity.getId().intValue() + "/user/" + user.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.activityId").value(activityUser.getActivity().getId()),
                        jsonPath("$.user.id").value(activityUser.getUser().getId()),
                        jsonPath("$.user.lastName").value(activityUser.getUser().getLastName()),
                        jsonPath("$.user.firstName").value(activityUser.getUser().getFirstName()),
                        jsonPath("$.startTime").value(activityUser.getStartTime().toString()),
                        jsonPath("$.stopTime").value(activityUser.getStopTime().toString()),
                        jsonPath("$.spentTime").value(activityUser.getSpentTime()),
                        jsonPath("$.status").value(activityUser.getStatus().name())
                );
    }

    @Test
    void getUserInActivityWithNotFoundExceptionTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 21.4);

        when(activityService.getUserInActivity(activity.getId(), user.getId())).thenThrow(new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));

        mockMvc.perform(get("/api/v1/activity/" + activity.getId().intValue() + "/user/" + user.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.ACTIVITY_NOT_FOUND)
                );
    }

    @Test
    void getUserInActivityWithExistenceExceptionTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 21.4);

        when(activityService.getUserInActivity(activity.getId(), user.getId())).thenThrow(new ExistenceException(ErrorMessage.USER_DOES_NOT_EXIST_IN_ACTIVITY));

        mockMvc.perform(get("/api/v1/activity/" + activity.getId().intValue() + "/user/" + user.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_DOES_NOT_EXIST_IN_ACTIVITY)
                );
    }

    @Test
    void addUserToActivityTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 0.0);
        ActivityUser activityUser = new ActivityUser(new ActivityUserKey(activity.getId(), user.getId()), activity,
                user, null, null, 0.0, ActivityUserStatus.NOT_STARTED);

        UserInActivityDto activityUserDto = ActivityUserMapper.INSTANCE.toUserInActivityDto(activityUser);

        when(activityService.addUserToActivity(activity.getId(), user.getId())).thenReturn(activityUserDto);

        mockMvc.perform(post("/api/v1/activity/" + activity.getId().intValue() + "/user/" + user.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.activityId").value(activityUser.getActivity().getId()),
                        jsonPath("$.user.id").value(activityUser.getUser().getId()),
                        jsonPath("$.user.lastName").value(activityUser.getUser().getLastName()),
                        jsonPath("$.user.firstName").value(activityUser.getUser().getFirstName()),
                        jsonPath("$.startTime").value(activityUser.getStartTime()),
                        jsonPath("$.stopTime").value(activityUser.getStopTime()),
                        jsonPath("$.spentTime").value(activityUser.getSpentTime()),
                        jsonPath("$.status").value(activityUser.getStatus().name())
                );
    }

    @Test
    void addUserToActivityWithNotFoundExceptionTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 0.0);

        when(activityService.addUserToActivity(activity.getId(), user.getId())).thenThrow(new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));

        mockMvc.perform(post("/api/v1/activity/" + activity.getId().intValue() + "/user/" + user.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.ACTIVITY_NOT_FOUND)
                );
    }

    @Test
    void addUserToActivityWithRestrictionExceptionTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 0.0);

        when(activityService.addUserToActivity(activity.getId(), user.getId())).thenThrow(new RestrictionException(ErrorMessage.ACTIVITY_IS_NOT_AVAILABLE));

        mockMvc.perform(post("/api/v1/activity/" + activity.getId().intValue() + "/user/" + user.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.ACTIVITY_IS_NOT_AVAILABLE)
                );
    }

    @Test
    void addUserToActivityWithExistenceExceptionTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 0.0);

        when(activityService.addUserToActivity(activity.getId(), user.getId())).thenThrow(new ExistenceException(ErrorMessage.USER_EXISTS_IN_ACTIVITY));

        mockMvc.perform(post("/api/v1/activity/" + activity.getId().intValue() + "/user/" + user.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_EXISTS_IN_ACTIVITY)
                );
    }

    @Test
    void removeUserFromActivityTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 0.0);

        doNothing().when(activityService).removeUserFromActivity(activity.getId(), user.getId());

        mockMvc.perform(delete("/api/v1/activity/" + activity.getId().intValue() + "/user/" + user.getId().intValue()))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(activityService, times(1)).removeUserFromActivity(activity.getId(), user.getId());
    }

    @Test
    void startActivityTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 0.0);
        ActivityUser activityUser = new ActivityUser(new ActivityUserKey(activity.getId(), user.getId()), activity,
                user, LocalDateTime.of(2022, 7, 25, 10, 31, 22), null,
                0.0, ActivityUserStatus.STARTED);

        UserInActivityDto activityUserDto = ActivityUserMapper.INSTANCE.toUserInActivityDto(activityUser);

        when(activityService.startActivity(activity.getId(), user.getId())).thenReturn(activityUserDto);

        mockMvc.perform(put("/api/v1/activity/" + activity.getId().intValue() + "/user/" + user.getId().intValue() + "/start"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.activityId").value(activityUser.getActivity().getId()),
                        jsonPath("$.user.id").value(activityUser.getUser().getId()),
                        jsonPath("$.user.lastName").value(activityUser.getUser().getLastName()),
                        jsonPath("$.user.firstName").value(activityUser.getUser().getFirstName()),
                        jsonPath("$.startTime").value(activityUser.getStartTime().toString()),
                        jsonPath("$.stopTime").value(activityUser.getStopTime()),
                        jsonPath("$.spentTime").value(activityUser.getSpentTime()),
                        jsonPath("$.status").value(activityUser.getStatus().name())
                );
    }

    @Test
    void startActivityWithNotFoundExceptionTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 0.0);

        when(activityService.startActivity(activity.getId(), user.getId())).thenThrow(new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));

        mockMvc.perform(put("/api/v1/activity/" + activity.getId().intValue() + "/user/" + user.getId().intValue() + "/start"))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.ACTIVITY_NOT_FOUND)
                );
    }

    @Test
    void startActivityWithExistenceExceptionTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(1, 0.0);

        when(activityService.startActivity(activity.getId(), user.getId())).thenThrow(new ExistenceException(ErrorMessage.USER_DOES_NOT_EXIST_IN_ACTIVITY));

        mockMvc.perform(put("/api/v1/activity/" + activity.getId().intValue() + "/user/" + user.getId().intValue() + "/start"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_DOES_NOT_EXIST_IN_ACTIVITY)
                );
    }

    @Test
    void stopActivityTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(2, 2.0);
        ActivityUser activityUser = new ActivityUser(new ActivityUserKey(activity.getId(), user.getId()), activity, user,
                LocalDateTime.of(2022, 7, 25, 10, 31, 22),
                LocalDateTime.of(2022, 7, 25, 12, 31, 22),
                2.0, ActivityUserStatus.STOPPED);

        UserInActivityDto activityUserDto = ActivityUserMapper.INSTANCE.toUserInActivityDto(activityUser);

        when(activityService.stopActivity(activity.getId(), user.getId())).thenReturn(activityUserDto);

        mockMvc.perform(put("/api/v1/activity/" + activity.getId().intValue() + "/user/" + user.getId().intValue() + "/stop"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.activityId").value(activityUser.getActivity().getId()),
                        jsonPath("$.user.id").value(activityUser.getUser().getId()),
                        jsonPath("$.user.lastName").value(activityUser.getUser().getLastName()),
                        jsonPath("$.user.firstName").value(activityUser.getUser().getFirstName()),
                        jsonPath("$.startTime").value(activityUser.getStartTime().toString()),
                        jsonPath("$.stopTime").value(activityUser.getStopTime().toString()),
                        jsonPath("$.spentTime").value(activityUser.getSpentTime()),
                        jsonPath("$.status").value(activityUser.getStatus().name())
                );
    }

    @Test
    void stopActivityWithNotFoundExceptionTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(2, 2.0);

        when(activityService.stopActivity(activity.getId(), user.getId())).thenThrow(new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));

        mockMvc.perform(put("/api/v1/activity/" + activity.getId().intValue() + "/user/" + user.getId().intValue() + "/stop"))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.ACTIVITY_NOT_FOUND)
                );
    }

    @Test
    void stopActivityWithExistenceExceptionTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());
        User user = UserDataUtilTest.getUser1(2, 2.0);

        when(activityService.stopActivity(activity.getId(), user.getId())).thenThrow(new ExistenceException(ErrorMessage.USER_DOES_NOT_EXIST_IN_ACTIVITY));

        mockMvc.perform(put("/api/v1/activity/" + activity.getId().intValue() + "/user/" + user.getId().intValue() + "/stop"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_DOES_NOT_EXIST_IN_ACTIVITY)
                );
    }

    @Test
    void updateActivityTest() throws Exception {
        User admin = UserDataUtilTest.getAdmin();

        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, admin);
        ActivityDto activityDto = ActivityMapper.INSTANCE.toActivityDto(activity);
        ActivityDto activityDtoWithInputData = ActivityDataUtilTest.getActivity1DtoForInputData(admin);
        activityDtoWithInputData.setCreatorId(null);

        ObjectMapper jsonMapper = new ObjectMapper();

        when(activityService.updateActivity(activity.getId(), activityDtoWithInputData)).thenReturn(activityDto);

        mockMvc.perform(
                        put("/api/v1/activity/" + activity.getId().intValue())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(activityDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(activity.getId()),
                        jsonPath("$.name").value(activityDtoWithInputData.getName()),
                        jsonPath("$.categories").value(hasSize(greaterThanOrEqualTo(1))),
                        jsonPath("$.description").value(activityDtoWithInputData.getDescription()),
                        jsonPath("$.image").value(activityDtoWithInputData.getImage())
                );
    }

    @Test
    void updateActivityWithNotFoundExceptionTest() throws Exception {
        User admin = UserDataUtilTest.getAdmin();

        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, admin);
        ActivityDto activityDtoWithInputData = ActivityDataUtilTest.getActivity1DtoForInputData(admin);
        activityDtoWithInputData.setCreatorId(null);

        ObjectMapper jsonMapper = new ObjectMapper();

        when(activityService.updateActivity(activity.getId(), activityDtoWithInputData)).thenThrow(new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));

        mockMvc.perform(
                        put("/api/v1/activity/" + activity.getId().intValue())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(activityDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.ACTIVITY_NOT_FOUND)
                );
    }

    @Test
    void updateActivityWithMethodArgumentNotValidExceptionTest() throws Exception {
        User admin = UserDataUtilTest.getAdmin();

        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, admin);
        ActivityDto activityDtoWithInputData = ActivityDataUtilTest.getActivity1DtoForInputData(admin);
        activityDtoWithInputData.setName("");
        activityDtoWithInputData.setCategories(List.of(CategoryMapper.INSTANCE.toCategoryDto(CategoryDataUtilTest.getCategory1())));
        activityDtoWithInputData.setDescription("");
        activityDtoWithInputData.setCreateTime(LocalDateTime.now());
        activityDtoWithInputData.setStatus(ActivityStatus.BY_ADMIN);

        int validations = 6;

        ObjectMapper jsonMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        mockMvc.perform(
                        put("/api/v1/activity/" + activity.getId().intValue())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(activityDtoWithInputData))
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value(hasSize(validations))
                );
    }

    @Test
    void deleteActivityTest() throws Exception {
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.BY_ADMIN, UserDataUtilTest.getAdmin());

        doNothing().when(activityService).deleteActivity(activity.getId());

        mockMvc.perform(delete("/api/v1/activity/" + activity.getId().intValue()))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(activityService, times(1)).deleteActivity(activity.getId());
    }

}
