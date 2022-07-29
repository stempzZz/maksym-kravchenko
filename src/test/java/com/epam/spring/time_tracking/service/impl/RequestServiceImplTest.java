package com.epam.spring.time_tracking.service.impl;

import com.epam.spring.time_tracking.dto.activity.ActivityDto;
import com.epam.spring.time_tracking.dto.request.RequestDto;
import com.epam.spring.time_tracking.dto.request.RequestForListDto;
import com.epam.spring.time_tracking.exception.ExistenceException;
import com.epam.spring.time_tracking.exception.NotFoundException;
import com.epam.spring.time_tracking.exception.RequestStatusForActivityException;
import com.epam.spring.time_tracking.exception.RestrictionException;
import com.epam.spring.time_tracking.mapper.ActivityMapper;
import com.epam.spring.time_tracking.mapper.RequestMapper;
import com.epam.spring.time_tracking.model.*;
import com.epam.spring.time_tracking.model.enums.status.ActivityStatus;
import com.epam.spring.time_tracking.model.enums.status.ActivityUserStatus;
import com.epam.spring.time_tracking.model.enums.status.RequestStatus;
import com.epam.spring.time_tracking.model.keys.ActivityUserKey;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.ActivityUserRepo;
import com.epam.spring.time_tracking.repository.RequestRepo;
import com.epam.spring.time_tracking.repository.UserRepo;
import com.epam.spring.time_tracking.service.CategoryService;
import com.epam.spring.time_tracking.test.util.ActivityDataUtilTest;
import com.epam.spring.time_tracking.test.util.CategoryDataUtilTest;
import com.epam.spring.time_tracking.test.util.RequestDataUtilTest;
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
public class RequestServiceImplTest {

    @InjectMocks
    private RequestServiceImpl requestService;

    @Mock
    private RequestRepo requestRepo;

    @Mock
    private ActivityRepo activityRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ActivityUserRepo activityUserRepo;

    @Mock
    private CategoryService categoryService;

    @Test
    void getRequestsTest() {
        User user = UserDataUtilTest.getUser1(1, 0);

        Activity activity1 = ActivityDataUtilTest.getActivity1(1, ActivityStatus.DEL_WAITING, user);
        Activity activity2 = ActivityDataUtilTest.getActivity2(0, ActivityStatus.ADD_WAITING, user);

        Request request1 = RequestDataUtilTest.getRequest1(activity1, false, RequestStatus.CONFIRMED);
        Request request2 = RequestDataUtilTest.getRequest2(activity1, true, RequestStatus.WAITING);
        Request request3 = RequestDataUtilTest.getRequest3(activity2, false, RequestStatus.WAITING);

        RequestForListDto request1Dto = RequestMapper.INSTANCE.toRequestDtoForList(request1);
        RequestForListDto request2Dto = RequestMapper.INSTANCE.toRequestDtoForList(request2);
        RequestForListDto request3Dto = RequestMapper.INSTANCE.toRequestDtoForList(request3);

        Page<Request> requests = new PageImpl<>(List.of(request1, request2, request3));
        Pageable pageable = PageRequest.of(1, 3, Sort.by("createTime").descending());

        when(requestRepo.findAll(pageable)).thenReturn(requests);
        List<RequestForListDto> result = requestService.getRequests(pageable);

        assertThat(result, hasSize(requests.getContent().size()));
        assertThat(result, hasItems(request1Dto, request2Dto, request3Dto));
    }

    @Test
    void getRequest() {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.DEL_WAITING, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.CONFIRMED);

        RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(request);

        when(requestRepo.findById(request.getId())).thenReturn(Optional.of(request));
        RequestDto result = requestService.getRequest(request.getId());

        assertThat(result, allOf(
                hasProperty("id", equalTo(requestDto.getId())),
                hasProperty("user", equalTo(requestDto.getUser())),
                hasProperty("activity", equalTo(requestDto.getActivity())),
                hasProperty("status", equalTo(requestDto.getStatus())),
                hasProperty("forDelete", equalTo(requestDto.isForDelete())),
                hasProperty("createTime", equalTo(requestDto.getCreateTime()))
        ));
    }

    @Test
    void getRequestWithNotFoundExceptionTest() {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.DEL_WAITING, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.CONFIRMED);

        when(requestRepo.findById(request.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getRequest(request.getId()));
    }

    @Test
    void createRequestToAddTest() {
        User user = UserDataUtilTest.getUser1(0, 0);
        ActivityDto activityDto = ActivityDataUtilTest.getActivity1DtoForInputData(user);
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.ADD_WAITING, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.WAITING);

        RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(request);

        List<Category> categories = List.of(CategoryDataUtilTest.getDefaultCategory());

        when(userRepo.findById(activityDto.getCreatorId())).thenReturn(Optional.of(user));
        when(categoryService.mapCategoriesIdsToCategories(activityDto.getCategoryIds())).thenReturn(categories);
        when(activityRepo.save(any())).thenReturn(activity);
        when(requestRepo.save(any())).thenReturn(request);
        RequestDto result = requestService.createRequestToAdd(activityDto);

        assertThat(result, allOf(
                hasProperty("id", equalTo(requestDto.getId())),
                hasProperty("user", equalTo(requestDto.getUser())),
                hasProperty("activity", equalTo(requestDto.getActivity())),
                hasProperty("status", equalTo(requestDto.getStatus())),
                hasProperty("forDelete", equalTo(requestDto.isForDelete())),
                hasProperty("createTime", equalTo(requestDto.getCreateTime()))
        ));
    }

    @Test
    void createRequestToAddWithUserNotFoundExceptionTest() {
        User user = UserDataUtilTest.getUser1(0, 0);
        ActivityDto activityDto = ActivityDataUtilTest.getActivity1DtoForInputData(user);

        when(userRepo.findById(activityDto.getCreatorId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.createRequestToAdd(activityDto));
    }

    @Test
    void createRequestToAddWithRestrictionExceptionTest() {
        User admin = UserDataUtilTest.getAdmin();
        ActivityDto activityDto = ActivityDataUtilTest.getActivity1DtoForInputData(admin);

        when(userRepo.findById(activityDto.getCreatorId())).thenReturn(Optional.of(admin));

        assertThrows(RestrictionException.class, () -> requestService.createRequestToAdd(activityDto));
    }

    @Test
    void createRequestToAddWithCategoryNotFoundExceptionTest() {
        User user = UserDataUtilTest.getUser1(0, 0);
        ActivityDto activityDto = ActivityDataUtilTest.getActivity1DtoForInputData(user);

        when(userRepo.findById(activityDto.getCreatorId())).thenReturn(Optional.of(user));
        when(categoryService.mapCategoriesIdsToCategories(activityDto.getCategoryIds())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> requestService.createRequestToAdd(activityDto));
    }

    @Test
    void createRequestToRemoveTest() {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_USER, user);

        Activity activityDelWaiting = ActivityDataUtilTest.getActivity1(1, ActivityStatus.DEL_WAITING, user);
        Request request = RequestDataUtilTest.getRequest1(activityDelWaiting, true, RequestStatus.WAITING);

        RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(request);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(requestRepo.requestForDeleteWithActivityExists(activity)).thenReturn(false);
        when(requestRepo.save(any())).thenReturn(request);
        RequestDto result = requestService.createRequestToRemove(activity.getId());

        assertThat(result, allOf(
                hasProperty("id", equalTo(requestDto.getId())),
                hasProperty("user", equalTo(requestDto.getUser())),
                hasProperty("activity", equalTo(requestDto.getActivity())),
                hasProperty("status", equalTo(requestDto.getStatus())),
                hasProperty("forDelete", equalTo(requestDto.isForDelete())),
                hasProperty("createTime", equalTo(requestDto.getCreateTime()))
        ));
        assertThat(activity.getStatus(), equalTo(ActivityStatus.DEL_WAITING));
    }

    @Test
    void createRequestToRemoveWithNotFoundExceptionTest() {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_USER, user);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.createRequestToRemove(activity.getId()));
    }

    @Test
    void createRequestToRemoveWithRestrictionExceptionWhereActivityIsNotAvailableTest() {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.ADD_WAITING, user);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));

        assertThrows(RestrictionException.class, () -> requestService.createRequestToRemove(activity.getId()));
    }

    @Test
    void createRequestToRemoveWithRestrictionExceptionWhereActivityIsNotByUserTest() {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_ADMIN, user);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));

        assertThrows(RestrictionException.class, () -> requestService.createRequestToRemove(activity.getId()));
    }

    @Test
    void createRequestToRemoveWithRestrictionExceptionWhereCreatorIsAdminTest() {
        User admin = UserDataUtilTest.getAdmin();
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_USER, admin);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));

        assertThrows(RestrictionException.class, () -> requestService.createRequestToRemove(activity.getId()));
    }

    @Test
    void createRequestToRemoveWithExistenceExceptionTest() {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_USER, user);

        when(activityRepo.findById(activity.getId())).thenReturn(Optional.of(activity));
        when(requestRepo.requestForDeleteWithActivityExists(activity)).thenReturn(true);

        assertThrows(ExistenceException.class, () -> requestService.createRequestToRemove(activity.getId()));
    }

    @Test
    void confirmRequestWhereIsForAddTest() {
        User user = UserDataUtilTest.getUser1(0, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.ADD_WAITING, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.WAITING);

        int peopleCountAfterConfirmation = activity.getPeopleCount() + 1;
        int activityCountAfterConfirmation = user.getActivityCount() + 1;

        User userAfterConfirmation = UserDataUtilTest.getUser1(activityCountAfterConfirmation, 0);
        Activity activityAfterConfirmation = ActivityDataUtilTest.getActivity1(peopleCountAfterConfirmation, ActivityStatus.BY_USER, userAfterConfirmation);
        ActivityUser activityUser = new ActivityUser(new ActivityUserKey(activityAfterConfirmation.getId(), userAfterConfirmation.getId()),
                activityAfterConfirmation, userAfterConfirmation, null, null, 0.0, ActivityUserStatus.NOT_STARTED);
        Request confirmedRequest = RequestDataUtilTest.getRequest1(activityAfterConfirmation, false, RequestStatus.CONFIRMED);

        RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(confirmedRequest);

        when(requestRepo.findById(request.getId())).thenReturn(Optional.of(request));
        when(activityUserRepo.save(any())).thenReturn(activityUser);
        when(requestRepo.save(any())).thenReturn(confirmedRequest);
        RequestDto result = requestService.confirmRequest(request.getId());

        assertThat(result, allOf(
                hasProperty("id", equalTo(requestDto.getId())),
                hasProperty("user", equalTo(requestDto.getUser())),
                hasProperty("activity", equalTo(requestDto.getActivity())),
                hasProperty("status", equalTo(requestDto.getStatus())),
                hasProperty("forDelete", equalTo(requestDto.isForDelete())),
                hasProperty("createTime", equalTo(requestDto.getCreateTime()))
        ));
        assertThat(request.getStatus(), equalTo(RequestStatus.CONFIRMED));
        assertThat(activity.getStatus(), equalTo(ActivityStatus.BY_USER));
        assertThat(activity.getPeopleCount(), greaterThan(0));
        assertThat(activity.getPeopleCount(), equalTo(peopleCountAfterConfirmation));
        assertThat(user.getActivityCount(), greaterThan(0));
        assertThat(user.getActivityCount(), equalTo(activityCountAfterConfirmation));
    }

    @Test
    void confirmRequestWhereIsForDeleteTest() {
        User user1 = UserDataUtilTest.getUser1(1, 0);
        User user2 = UserDataUtilTest.getUser2(2, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(2, ActivityStatus.DEL_WAITING, user1);
        activity.setUsers(
                List.of(
                        new ActivityUser(new ActivityUserKey(activity.getId(), user1.getId()), activity, user1,
                                null, null, 0.0, ActivityUserStatus.NOT_STARTED),
                        new ActivityUser(new ActivityUserKey(activity.getId(), user2.getId()), activity, user2,
                                null, null, 0.0, ActivityUserStatus.NOT_STARTED)
                )
        );
        Request request = RequestDataUtilTest.getRequest1(activity, true, RequestStatus.WAITING);

        int user1ActivityCountAfterConfirmation = user1.getActivityCount() - 1;
        int user2ActivityCountAfterConfirmation = user2.getActivityCount() - 1;

        User user1AfterConfirmation = UserDataUtilTest.getUser1(user1ActivityCountAfterConfirmation, 0);
        Activity activityAfterConfirmation = ActivityDataUtilTest.getActivity1(0, ActivityStatus.DEL_CONFIRMED, user1AfterConfirmation);
        Request confirmedRequest = RequestDataUtilTest.getRequest1(activityAfterConfirmation, true, RequestStatus.CONFIRMED);

        RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(confirmedRequest);

        when(requestRepo.findById(request.getId())).thenReturn(Optional.of(request));
        when(activityUserRepo.findAllByActivity(activity)).thenReturn(activity.getUsers());
        when(requestRepo.save(any())).thenReturn(confirmedRequest);
        RequestDto result = requestService.confirmRequest(request.getId());

        verify(activityUserRepo, times(1)).deleteAll(activity.getUsers());
        assertThat(result, allOf(
                hasProperty("id", equalTo(requestDto.getId())),
                hasProperty("user", equalTo(requestDto.getUser())),
                hasProperty("activity", equalTo(requestDto.getActivity())),
                hasProperty("status", equalTo(requestDto.getStatus())),
                hasProperty("forDelete", equalTo(requestDto.isForDelete())),
                hasProperty("createTime", equalTo(requestDto.getCreateTime()))
        ));
        assertThat(request.getStatus(), equalTo(RequestStatus.CONFIRMED));
        assertThat(activity.getStatus(), equalTo(ActivityStatus.DEL_CONFIRMED));
        assertThat(activity.getPeopleCount(), is(0));
        assertThat(user1.getActivityCount(), equalTo(user1ActivityCountAfterConfirmation));
        assertThat(user2.getActivityCount(), equalTo(user2ActivityCountAfterConfirmation));
    }

    @Test
    void confirmRequestWithNotFoundExceptionTest() {
        User user = UserDataUtilTest.getUser1(0, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.ADD_WAITING, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.WAITING);

        when(requestRepo.findById(request.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.confirmRequest(request.getId()));
    }

    @Test
    void confirmRequestWithRequestStatusForActivityExceptionWhereRequestIsConfirmedTest() {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_USER, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.CONFIRMED);

        when(requestRepo.findById(request.getId())).thenReturn(Optional.of(request));

        assertThrows(RequestStatusForActivityException.class, () -> requestService.confirmRequest(request.getId()));
    }

    @Test
    void confirmRequestWithRequestStatusForActivityExceptionWhereRequestIsDeclinedTest() {
        User user = UserDataUtilTest.getUser1(0, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.ADD_DECLINED, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.DECLINED);

        when(requestRepo.findById(request.getId())).thenReturn(Optional.of(request));

        assertThrows(RequestStatusForActivityException.class, () -> requestService.confirmRequest(request.getId()));
    }

    @Test
    void declineRequestForAddTest() {
        User user = UserDataUtilTest.getUser1(0, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.ADD_WAITING, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.WAITING);

        Activity activityAfterDeclining = ActivityDataUtilTest.getActivity1(0, ActivityStatus.ADD_DECLINED, user);
        Request declinedRequest = RequestDataUtilTest.getRequest1(activityAfterDeclining, false, RequestStatus.DECLINED);

        RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(declinedRequest);

        when(requestRepo.findById(request.getId())).thenReturn(Optional.of(request));
        when(requestRepo.save(any())).thenReturn(declinedRequest);
        RequestDto result = requestService.declineRequest(request.getId());

        assertThat(result, allOf(
                hasProperty("id", equalTo(requestDto.getId())),
                hasProperty("user", equalTo(requestDto.getUser())),
                hasProperty("activity", equalTo(requestDto.getActivity())),
                hasProperty("status", equalTo(requestDto.getStatus())),
                hasProperty("forDelete", equalTo(requestDto.isForDelete())),
                hasProperty("createTime", equalTo(requestDto.getCreateTime()))
        ));
        assertThat(request.getStatus(), equalTo(RequestStatus.DECLINED));
        assertThat(activity.getStatus(), equalTo(ActivityStatus.ADD_DECLINED));
    }

    @Test
    void declineRequestForDeleteTest() {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(2, ActivityStatus.DEL_WAITING, user);
        Request request = RequestDataUtilTest.getRequest1(activity, true, RequestStatus.WAITING);

        Activity activityAfterDeclining = ActivityDataUtilTest.getActivity1(2, ActivityStatus.BY_USER, user);
        Request declinedRequest = RequestDataUtilTest.getRequest1(activityAfterDeclining, true, RequestStatus.DECLINED);

        RequestDto requestDto = RequestMapper.INSTANCE.toRequestDto(declinedRequest);

        when(requestRepo.findById(request.getId())).thenReturn(Optional.of(request));
        when(requestRepo.save(any())).thenReturn(declinedRequest);
        RequestDto result = requestService.declineRequest(request.getId());

        assertThat(result, allOf(
                hasProperty("id", equalTo(requestDto.getId())),
                hasProperty("user", equalTo(requestDto.getUser())),
                hasProperty("activity", equalTo(requestDto.getActivity())),
                hasProperty("status", equalTo(requestDto.getStatus())),
                hasProperty("forDelete", equalTo(requestDto.isForDelete())),
                hasProperty("createTime", equalTo(requestDto.getCreateTime()))
        ));
        assertThat(request.getStatus(), equalTo(RequestStatus.DECLINED));
        assertThat(activity.getStatus(), equalTo(ActivityStatus.BY_USER));
    }

    @Test
    void declineRequestWithNotFoundExceptionTest() {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(2, ActivityStatus.DEL_WAITING, user);
        Request request = RequestDataUtilTest.getRequest1(activity, true, RequestStatus.WAITING);

        when(requestRepo.findById(request.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.declineRequest(request.getId()));
    }

    @Test
    void declineRequestWithRequestStatusForActivityExceptionWhereRequestIsConfirmedTest() {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_USER, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.CONFIRMED);

        when(requestRepo.findById(request.getId())).thenReturn(Optional.of(request));

        assertThrows(RequestStatusForActivityException.class, () -> requestService.declineRequest(request.getId()));
    }

    @Test
    void declineRequestWithRequestStatusForActivityExceptionWhereRequestIsDeclinedTest() {
        User user = UserDataUtilTest.getUser1(0, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.ADD_DECLINED, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.DECLINED);

        when(requestRepo.findById(request.getId())).thenReturn(Optional.of(request));

        assertThrows(RequestStatusForActivityException.class, () -> requestService.declineRequest(request.getId()));
    }

    @Test
    void deleteRequestWhereRequestForAddIsConfirmedTest() {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_USER, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.CONFIRMED);

        when(requestRepo.findById(request.getId())).thenReturn(Optional.of(request));
        requestService.deleteRequest(request.getId());

        verify(requestRepo, times(1)).delete(request);
    }

    @Test
    void deleteRequestWhereRequestForDeleteIsDeclinedTest() {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_USER, user);
        Request request = RequestDataUtilTest.getRequest1(activity, true, RequestStatus.DECLINED);

        when(requestRepo.findById(request.getId())).thenReturn(Optional.of(request));
        requestService.deleteRequest(request.getId());

        verify(requestRepo, times(1)).delete(request);
    }

    @Test
    void deleteRequestWhereRequestForAddIsDeclinedTest() {
        User user = UserDataUtilTest.getUser1(0, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(0, ActivityStatus.ADD_DECLINED, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.DECLINED);

        when(requestRepo.findById(request.getId())).thenReturn(Optional.of(request));
        requestService.deleteRequest(request.getId());

        verify(requestRepo, times(1)).deleteAllByActivity(request.getActivity());
        verify(activityRepo, times(1)).deleteById(request.getActivity().getId());
    }

    @Test
    void deleteRequestWhereRequestForDeleteIsConfirmedTest() {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.DEL_CONFIRMED, user);
        Request request = RequestDataUtilTest.getRequest1(activity, true, RequestStatus.CONFIRMED);

        when(requestRepo.findById(request.getId())).thenReturn(Optional.of(request));
        requestService.deleteRequest(request.getId());

        verify(requestRepo, times(1)).deleteAllByActivity(request.getActivity());
        verify(activityRepo, times(1)).deleteById(request.getActivity().getId());
    }

    @Test
    void deleteRequestWithNotFoundExceptionTest() {
        User user = UserDataUtilTest.getUser1(1, 0);
        Activity activity = ActivityDataUtilTest.getActivity1(1, ActivityStatus.BY_USER, user);
        Request request = RequestDataUtilTest.getRequest1(activity, false, RequestStatus.CONFIRMED);

        when(requestRepo.findById(request.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.deleteRequest(request.getId()));
    }

}
