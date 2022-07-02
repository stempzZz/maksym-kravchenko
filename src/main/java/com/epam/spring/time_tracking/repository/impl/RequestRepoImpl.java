package com.epam.spring.time_tracking.repository.impl;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Request;
import com.epam.spring.time_tracking.model.User;
import com.epam.spring.time_tracking.model.UserActivity;
import com.epam.spring.time_tracking.repository.ActivityRepo;
import com.epam.spring.time_tracking.repository.RequestRepo;
import com.epam.spring.time_tracking.repository.UserActivityRepo;
import com.epam.spring.time_tracking.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestRepoImpl implements RequestRepo {

    private final List<Request> requestList = new ArrayList<>();
    private final ActivityRepo activityRepo;
    private final UserRepo userRepo;
    private final UserActivityRepo userActivityRepo;
    private int idCounter;

    @Override
    public Request createRequestForAdd(Activity activity) {
        Activity activityForAdd = activityRepo.createActivityForRequest(activity);
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
    public Request createRequestForRemove(Activity activity) {
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
        Request request = requestList.stream()
                .filter(r -> r.getId() == requestId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("request is not found"));
        request.setStatus(Request.Status.CONFIRMED);
        Activity activity = activityRepo.getActivityById(request.getActivityId());
        if (request.isForDelete()) {
            activity.setStatus(Activity.Status.DEL_CONFIRMED);
            List<User> users = userActivityRepo.getActivityUsers(activity.getId()).stream()
                    .map(UserActivity::getUser)
                    .collect(Collectors.toList());
            userActivityRepo.deleteActivity(activity.getId());
            users.forEach(user -> user.setActivityCount(user.getActivityCount() - 1));
        } else {
            activity.setStatus(Activity.Status.BY_USER);
            userActivityRepo.addUserToActivity(activity.getId(), activity.getCreatorId());
        }
        return request;
    }
}
