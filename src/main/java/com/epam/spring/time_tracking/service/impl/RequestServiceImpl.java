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
import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.ActivityUser;
import com.epam.spring.time_tracking.model.Request;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.enums.status.ActivityStatus;
import com.epam.spring.time_tracking.model.enums.status.RequestStatus;
import com.epam.spring.time_tracking.model.errors.ErrorMessage;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.ActivityUserRepo;
import com.epam.spring.time_tracking.repository.RequestRepo;
import com.epam.spring.time_tracking.repository.UserRepo;
import com.epam.spring.time_tracking.service.CategoryService;
import com.epam.spring.time_tracking.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepo requestRepo;
    private final ActivityRepo activityRepo;
    private final UserRepo userRepo;
    private final ActivityUserRepo activityUserRepo;

    private final CategoryService categoryService;

    @Override
    public List<RequestForListDto> getRequests(Pageable pageable) {
        log.info("Getting requests");

        return requestRepo.findAll(pageable).stream()
                .map(RequestMapper.INSTANCE::toRequestDtoForList)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto getRequest(Long requestId) {
        log.info("Getting request with id: {}", requestId);

        Request request = requestRepo.findById(requestId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.REQUEST_NOT_FOUND));
        log.info("Received request (id={}): {}", requestId, request);
        return RequestMapper.INSTANCE.toRequestDto(request);
    }

    @Transactional
    @Override
    public RequestDto createRequestToAdd(ActivityDto activityDto) {
        log.info("Creating request to add an activity: {}", activityDto);

        User creator = userRepo.findById(activityDto.getCreatorId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        if (creator.isAdmin())
            throw new RestrictionException(ErrorMessage.CREATOR_IS_NOT_A_REGULAR_USER);

        Activity activity = ActivityMapper.INSTANCE.fromActivityDto(activityDto);
        activity.setStatus(ActivityStatus.ADD_WAITING);
        activity.setCreator(creator);
        activity.setCategories(categoryService.mapCategoriesIdsToCategories(activityDto.getCategoryIds()));
        activity = activityRepo.save(activity);

        Request request = createRequest(activity, false);
        log.info("Request to add an activity is created: {}", request);
        return RequestMapper.INSTANCE.toRequestDto(request);
    }

    @Transactional
    @Override
    public RequestDto createRequestToRemove(Long activityId) {
        log.info("Creating request to remove an activity with id: {}", activityId);

        Activity activity = activityRepo.findById(activityId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ACTIVITY_NOT_FOUND));

        if (activity.getStatus().equals(ActivityStatus.ADD_WAITING) ||
                activity.getStatus().equals(ActivityStatus.ADD_DECLINED) ||
                activity.getStatus().equals(ActivityStatus.DEL_CONFIRMED))
            throw new RestrictionException(ErrorMessage.ACTIVITY_IS_NOT_AVAILABLE);
        else if (activity.getStatus().equals(ActivityStatus.BY_ADMIN))
            throw new RestrictionException(ErrorMessage.ACTIVITY_IS_NOT_BY_USER);

        if (activity.getCreator().isAdmin())
            throw new RestrictionException(ErrorMessage.CREATOR_IS_NOT_A_REGULAR_USER);
        if (requestRepo.requestForDeleteWithActivityExists(activity))
            throw new ExistenceException(ErrorMessage.REQUEST_EXISTS_WITH_ACTIVITY);

        activity.setStatus(ActivityStatus.DEL_WAITING);

        Request request = createRequest(activity, true);
        log.info("Request to remove an activity is created: {}", request);
        return RequestMapper.INSTANCE.toRequestDto(request);
    }

    @Transactional
    @Override
    public RequestDto confirmRequest(Long requestId) {
        log.info("Confirmation of request with id: {}", requestId);

        Request request = requestRepo.findById(requestId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.REQUEST_NOT_FOUND));

        if (request.getStatus().equals(RequestStatus.CONFIRMED) || request.getStatus().equals(RequestStatus.DECLINED))
            throw new RequestStatusForActivityException();

        request.setStatus(RequestStatus.CONFIRMED);

        Activity activity = request.getActivity();

        if (request.isForDelete()) {
            activity.setStatus(ActivityStatus.DEL_CONFIRMED);

            List<ActivityUser> activityUsers = activityUserRepo.findAllByActivity(activity);
            activityUsers.forEach(activityUser -> {
                activityUser.getUser().setActivityCount(activityUser.getUser().getActivityCount() - 1);
                activityUser.getActivity().setPeopleCount(activityUser.getActivity().getPeopleCount() - 1);
            });

            activityUserRepo.deleteAll(activityUsers);
        } else {
            activity.setStatus(ActivityStatus.BY_USER);

            activity.setPeopleCount(activity.getPeopleCount() + 1);
            activity.getCreator().setActivityCount(activity.getCreator().getActivityCount() + 1);

            ActivityUser activityUser = new ActivityUser();

            activityUser.setActivity(activity);
            activityUser.setUser(activity.getCreator());

            activityUserRepo.save(activityUser);
        }

        request = requestRepo.save(request);
        log.info("Request (id={}) is confirmed", requestId);
        return RequestMapper.INSTANCE.toRequestDto(request);
    }

    @Transactional
    @Override
    public RequestDto declineRequest(Long requestId) {
        log.info("Declining of request with id: {}", requestId);

        Request request = requestRepo.findById(requestId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.REQUEST_NOT_FOUND));

        if (request.getStatus().equals(RequestStatus.CONFIRMED) || request.getStatus().equals(RequestStatus.DECLINED))
            throw new RequestStatusForActivityException();

        request.setStatus(RequestStatus.DECLINED);

        if (request.isForDelete())
            request.getActivity().setStatus(ActivityStatus.BY_USER);
        else
            request.getActivity().setStatus(ActivityStatus.ADD_DECLINED);

        request = requestRepo.save(request);
        log.info("Request (id={}) is declined", requestId);
        return RequestMapper.INSTANCE.toRequestDto(request);
    }

    @Transactional
    @Override
    public void deleteRequest(Long requestId) {
        log.info("Deleting request with id: {}", requestId);

        Request request = requestRepo.findById(requestId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.REQUEST_NOT_FOUND));

        if ((request.isForDelete() && request.getStatus().equals(RequestStatus.CONFIRMED)) ||
                (!request.isForDelete() && !request.getStatus().equals(RequestStatus.CONFIRMED))) {
            requestRepo.deleteAllByActivity(request.getActivity());
            activityRepo.deleteById(request.getActivity().getId());
        } else {
            requestRepo.delete(request);
        }
        log.info("Request (id={}) is deleted", requestId);
    }

    private Request createRequest(Activity activity, boolean isForDelete) {
        Request request = new Request();
        request.setActivity(activity);
        request.setStatus(RequestStatus.WAITING);
        request.setForDelete(isForDelete);
        request.setCreateTime(LocalDateTime.now());
        return requestRepo.save(request);
    }

}
