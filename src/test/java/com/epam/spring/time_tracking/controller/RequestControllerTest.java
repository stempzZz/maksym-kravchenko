package com.epam.spring.time_tracking.controller;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.request.RequestDto;
import com.epam.spring.time_tracking.dto.request.RequestForListDto;
import com.epam.spring.time_tracking.exception.ExistenceException;
import com.epam.spring.time_tracking.exception.NotFoundException;
import com.epam.spring.time_tracking.exception.RequestStatusForActivityException;
import com.epam.spring.time_tracking.exception.RestrictionException;
import com.epam.spring.time_tracking.mapper.CategoryMapper;
import com.epam.spring.time_tracking.mapper.RequestMapper;
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Request;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.enums.status.ActivityStatus;
import com.epam.spring.time_tracking.model.enums.status.RequestStatus;
import com.epam.spring.time_tracking.model.errors.ErrorMessage;
import com.epam.spring.time_tracking.service.RequestService;
import com.epam.spring.time_tracking.test.util.ActivityDataUtilTest;
import com.epam.spring.time_tracking.test.util.CategoryDataUtilTest;
import com.epam.spring.time_tracking.test.util.RequestDataUtilTest;
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
@WebMvcTest(RequestController.class)
public class RequestControllerTest {

    @MockBean
    private RequestService requestService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestsTest() throws Exception {
        User user = UserDataUtilTest.getUser1(1, 0);

        Activity activity1 = ActivityDataUtilTest.getActivity1(1, ActivityStatus.DEL_WAITING, user);
        Activity activity2 = ActivityDataUtilTest.getActivity2(0, ActivityStatus.ADD_WAITING, user);

        RequestForListDto request1Dto = RequestMapper.INSTANCE.toRequestDtoForList(
                RequestDataUtilTest.getRequest1(activity1, false, RequestStatus.CONFIRMED));
        RequestForListDto request2Dto = RequestMapper.INSTANCE.toRequestDtoForList(
                RequestDataUtilTest.getRequest2(activity1, true, RequestStatus.WAITING));
        RequestForListDto request3Dto = RequestMapper.INSTANCE.toRequestDtoForList(
                RequestDataUtilTest.getRequest3(activity2, false, RequestStatus.WAITING));

        List<RequestForListDto> requests = List.of(request1Dto, request2Dto, request3Dto);

        when(requestService.getRequests(any())).thenReturn(requests);

        mockMvc.perform(get("/api/v1/request"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value(hasSize(greaterThanOrEqualTo(0))),
                        jsonPath("$").value(hasSize(lessThanOrEqualTo(10))),
                        jsonPath("$").value(hasSize(requests.size())),
                        jsonPath("$[0].id").value(request1Dto.getId()),
                        jsonPath("$[1].id").value(request2Dto.getId()),
                        jsonPath("$[2].id").value(request3Dto.getId())
                );
    }

    @Test
    void getRequestTest() throws Exception {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.DEL_WAITING, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.CONFIRMED);

        RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(request);

        when(requestService.getRequest(request.getId())).thenReturn(requestDto);

        mockMvc.perform(get("/api/v1/request/" + request.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(request.getId()),
                        jsonPath("$.user.id").value(request.getActivity().getCreator().getId()),
                        jsonPath("$.activity.id").value(request.getActivity().getId()),
                        jsonPath("$.status").value(request.getStatus().name()),
                        jsonPath("$.forDelete").value(request.isForDelete()),
                        jsonPath("$.createTime").value(notNullValue())
                );
    }

    @Test
    void getRequestWithNotFoundExceptionTest() throws Exception {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.DEL_WAITING, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.CONFIRMED);

        when(requestService.getRequest(request.getId())).thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        mockMvc.perform(get("/api/v1/request/" + request.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_NOT_FOUND)
                );
    }

    @Test
    void getRequestWithRestrictionExceptionTest() throws Exception {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.DEL_WAITING, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.CONFIRMED);

        when(requestService.getRequest(request.getId())).thenThrow(new RestrictionException(ErrorMessage.CREATOR_IS_NOT_A_REGULAR_USER));

        mockMvc.perform(get("/api/v1/request/" + request.getId().intValue()))
                .andDo(print())
                .andExpectAll(
                        status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.CREATOR_IS_NOT_A_REGULAR_USER)
                );
    }

    @Test
    void createRequestToAdd() throws Exception {
        User user = UserDataUtilTest.getUser1(0, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.ADD_WAITING, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.WAITING);

        ActivityDto activityDto = ActivityDataUtilTest.getActivity1DtoForInputData(user);

        RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(request);

        ObjectMapper jsonMapper = new ObjectMapper();

        when(requestService.createRequestToAdd(activityDto)).thenReturn(requestDto);

        mockMvc.perform(
                        post("/api/v1/request/activity/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(activityDto))
                )
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.user.id").value(request.getActivity().getCreator().getId()),
                        jsonPath("$.activity.id").value(request.getActivity().getId()),
                        jsonPath("$.status").value(request.getStatus().name()),
                        jsonPath("$.forDelete").value(request.isForDelete()),
                        jsonPath("$.createTime").value(notNullValue())
                );
    }

    @Test
    void createRequestToAddWithNotFoundExceptionTest() throws Exception {
        User user = UserDataUtilTest.getUser1(0, 0);
        ActivityDto activityDto = ActivityDataUtilTest.getActivity1DtoForInputData(user);

        ObjectMapper jsonMapper = new ObjectMapper();

        when(requestService.createRequestToAdd(activityDto)).thenThrow(new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        mockMvc.perform(
                        post("/api/v1/request/activity/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(activityDto))
                )
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.USER_NOT_FOUND)
                );
    }

    @Test
    void createRequestToAddWithRestrictionExceptionTest() throws Exception {
        User admin = UserDataUtilTest.getAdmin();
        ActivityDto activityDto = ActivityDataUtilTest.getActivity1DtoForInputData(admin);

        ObjectMapper jsonMapper = new ObjectMapper();

        when(requestService.createRequestToAdd(activityDto)).thenThrow(new RestrictionException(ErrorMessage.CREATOR_IS_NOT_A_REGULAR_USER));

        mockMvc.perform(
                        post("/api/v1/request/activity/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(activityDto))
                )
                .andDo(print())
                .andExpectAll(
                        status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.CREATOR_IS_NOT_A_REGULAR_USER)
                );
    }

    @Test
    void createRequestToAddWithMethodArgumentNotValidExceptionTest() throws Exception {
        User user = UserDataUtilTest.getUser1(0, 0);
        ActivityDto activityDto = ActivityDataUtilTest.getActivity1DtoForInputData(user);
        activityDto.setName("");
        activityDto.setCategories(List.of(CategoryMapper.INSTANCE.toCategoryDto(CategoryDataUtilTest.getCategory1())));
        activityDto.setDescription("");
        activityDto.setCreatorId(null);
        activityDto.setCreateTime(LocalDateTime.now());
        activityDto.setStatus(ActivityStatus.BY_ADMIN);

        int validations = 6;

        ObjectMapper jsonMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        mockMvc.perform(
                        post("/api/v1/request/activity/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(activityDto))
                )
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$").value(hasSize(validations))
                );
    }

    @Test
    void createRequestToRemoveTest() throws Exception {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.DEL_WAITING, user);
        Request request = RequestDataUtilTest.getRequest1(activity, true, RequestStatus.WAITING);

        RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(request);

        when(requestService.createRequestToRemove(activity.getId())).thenReturn(requestDto);

        mockMvc.perform(post("/api/v1/request/activity/" + activity.getId() + "/remove"))
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.user.id").value(request.getActivity().getCreator().getId()),
                        jsonPath("$.activity.id").value(request.getActivity().getId()),
                        jsonPath("$.status").value(request.getStatus().name()),
                        jsonPath("$.forDelete").value(request.isForDelete()),
                        jsonPath("$.createTime").value(notNullValue())
                );
    }

    @Test
    void createRequestToRemoveWithNotFoundExceptionTest() throws Exception {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_USER, user);

        when(requestService.createRequestToRemove(activity.getId())).thenThrow(new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));

        mockMvc.perform(post("/api/v1/request/activity/" + activity.getId() + "/remove"))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.ACTIVITY_NOT_FOUND)
                );
    }

    @Test
    void createRequestToRemoveWithRestrictionExceptionTest() throws Exception {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.ADD_WAITING, user);

        when(requestService.createRequestToRemove(activity.getId())).thenThrow(new RestrictionException(ErrorMessage.ACTIVITY_IS_NOT_AVAILABLE));

        mockMvc.perform(post("/api/v1/request/activity/" + activity.getId() + "/remove"))
                .andDo(print())
                .andExpectAll(
                        status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.ACTIVITY_IS_NOT_AVAILABLE)
                );
    }

    @Test
    void createRequestToRemoveWithExistenceExceptionTest() throws Exception {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_USER, user);

        when(requestService.createRequestToRemove(activity.getId())).thenThrow(new ExistenceException(ErrorMessage.REQUEST_EXISTS_WITH_ACTIVITY));

        mockMvc.perform(post("/api/v1/request/activity/" + activity.getId() + "/remove"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.REQUEST_EXISTS_WITH_ACTIVITY)
                );
    }

    @Test
    void confirmRequestTest() throws Exception {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_USER, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.CONFIRMED);

        RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(request);

        when(requestService.confirmRequest(request.getId())).thenReturn(requestDto);

        mockMvc.perform(post("/api/v1/request/" + request.getId().intValue() + "/confirm"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(request.getId()),
                        jsonPath("$.user.id").value(request.getActivity().getCreator().getId()),
                        jsonPath("$.activity.id").value(request.getActivity().getId()),
                        jsonPath("$.status").value(request.getStatus().name()),
                        jsonPath("$.forDelete").value(request.isForDelete()),
                        jsonPath("$.createTime").value(notNullValue())
                );
    }

    @Test
    void confirmRequestWithNotFoundExceptionTest() throws Exception {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_USER, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.WAITING);

        when(requestService.confirmRequest(request.getId())).thenThrow(new NotFoundException(ErrorMessage.REQUEST_NOT_FOUND));

        mockMvc.perform(post("/api/v1/request/" + request.getId().intValue() + "/confirm"))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.REQUEST_NOT_FOUND)
                );
    }

    @Test
    void confirmRequestWithRequestStatusForActivityExceptionTest() throws Exception {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_USER, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.CONFIRMED);

        when(requestService.confirmRequest(request.getId())).thenThrow(new RequestStatusForActivityException());

        mockMvc.perform(post("/api/v1/request/" + request.getId().intValue() + "/confirm"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    void declineRequestTest() throws Exception {
        User user = UserDataUtilTest.getUser1(0, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.ADD_DECLINED, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.DECLINED);

        RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(request);

        when(requestService.declineRequest(request.getId())).thenReturn(requestDto);

        mockMvc.perform(post("/api/v1/request/" + request.getId().intValue() + "/decline"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(request.getId()),
                        jsonPath("$.user.id").value(request.getActivity().getCreator().getId()),
                        jsonPath("$.activity.id").value(request.getActivity().getId()),
                        jsonPath("$.status").value(request.getStatus().name()),
                        jsonPath("$.forDelete").value(request.isForDelete()),
                        jsonPath("$.createTime").value(notNullValue())
                );
    }

    @Test
    void declineRequestWithNotFoundExceptionTest() throws Exception {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_USER, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.WAITING);

        when(requestService.declineRequest(request.getId())).thenThrow(new NotFoundException(ErrorMessage.REQUEST_NOT_FOUND));

        mockMvc.perform(post("/api/v1/request/" + request.getId().intValue() + "/decline"))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").value(ErrorMessage.REQUEST_NOT_FOUND)
                );
    }

    @Test
    void declineRequestWithRequestStatusForActivityExceptionTest() throws Exception {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_USER, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.CONFIRMED);

        when(requestService.declineRequest(request.getId())).thenThrow(new RequestStatusForActivityException());

        mockMvc.perform(post("/api/v1/request/" + request.getId().intValue() + "/decline"))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    void deleteRequestTest() throws Exception {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_USER, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.CONFIRMED);

        doNothing().when(requestService).deleteRequest(request.getId());

        mockMvc.perform(delete("/api/v1/request/" + request.getId().intValue()))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(requestService, times(1)).deleteRequest(request.getId());
    }

}
