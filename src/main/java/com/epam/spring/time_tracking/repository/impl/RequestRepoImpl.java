package com.epam.spring.time_tracking.repository.impl;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Request;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.UserActivity;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.RequestRepo;
import com.epam.spring.time_tracking.repository.UserActivityRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestRepoImpl implements RequestRepo {

    private final List<Request> requestList = new ArrayList<>();
    private final ActivityRepo activityRepo;
    private final UserActivityRepo userActivityRepo;
    private int idCounter;

    @Override
    public List<Request> getRequests() {
        log.info("Getting requests");
        return requestList;
    }

    @Override
    public Request getRequest(int requestId) {
        log.info("Getting request with id: {}", requestId);
        return requestList.stream()
                .filter(r -> r.getId() == requestId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("request is not found"));
    }

    @Override
    public Request createRequestToAdd(Activity activity) {
        log.info("Creating request to add an activity: {}", activity);
        Activity activityForAdd = activityRepo.createActivity(activity, true);
        Request request = Request.builder()
                .id(++idCounter)
                .activityId(activityForAdd.getId())
                .status(Request.Status.WAITING)
                .forDelete(false)
                .createTime(LocalDateTime.now())
                .build();
        requestList.add(request);
        return request;
    }

    @Override
    public Request createRequestToRemove(Activity activity) {
        log.info("Creating request to remove an activity: {}", activity);
        Request request = Request.builder()
                .id(++idCounter)
                .activityId(activity.getId())
                .status(Request.Status.WAITING)
                .forDelete(true)
                .createTime(LocalDateTime.now())
                .build();
        activity.setStatus(Activity.Status.DEL_WAITING);
        requestList.add(request);
        return request;
    }

    @Override
    public Request confirmRequest(int requestId) {
        log.info("Confirmation of request with id: {}", requestId);
        Request request = getRequest(requestId);
        request.setStatus(Request.Status.CONFIRMED);
        Activity activity = activityRepo.getActivityById(request.getActivityId());
        if (request.isForDelete()) {
            activity.setStatus(Activity.Status.DEL_CONFIRMED);
            List<User> users = userActivityRepo.getActivityUsers(activity.getId()).stream()
                    .map(UserActivity::getUser)
                    .collect(Collectors.toList());
            userActivityRepo.deleteActivity(activity.getId());
            users.forEach(user -> {
                user.setActivityCount(user.getActivityCount() - 1);
                activity.setPeopleCount(activity.getPeopleCount() - 1);
            });
        } else {
            activity.setStatus(Activity.Status.BY_USER);
            userActivityRepo.addUserToActivity(activity.getId(), activity.getCreatorId());
        }
        return request;
    }

    @Override
    public Request declineRequest(int requestId) {
        log.info("Declining of request with id: {}", requestId);
        Request request = getRequest(requestId);
        request.setStatus(Request.Status.DECLINED);
        Activity activity = activityRepo.getActivityById(request.getActivityId());
        if (request.isForDelete())
            activity.setStatus(Activity.Status.BY_USER);
        else
            activity.setStatus(Activity.Status.ADD_DECLINED);
        return request;
    }

    @Override
    public void deleteRequest(int requestId) {
        log.info("Deleting request with id: {}", requestId);
        Request request = getRequest(requestId);
        if ((request.isForDelete() && request.getStatus().equals(Request.Status.CONFIRMED)) ||
                (!request.isForDelete() && !request.getStatus().equals(Request.Status.CONFIRMED)))
            activityRepo.deleteActivity(request.getActivityId());
        requestList.remove(request);
    }

    @Override
    public void deleteRequestsWithActivity(int activityId) {
        log.info("Deleting requests with activity (id={})", activityId);
        requestList.removeIf(request -> request.getActivityId() == activityId);
    }
}
