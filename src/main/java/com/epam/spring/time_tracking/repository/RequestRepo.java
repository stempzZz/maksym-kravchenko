package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Request;

import java.util.List;

public interface RequestRepo {
    Request createRequestForAdd(Activity activity);

    Request createRequestForRemove(Activity activity);

    Request confirmRequest(int requestId);

    Request declineRequest(int requestId);

    Request getRequest(int requestId);

    List<Request> getRequests();

    void deleteRequestsWithActivity(int activityId);

    void deleteRequest(int requestId);
}
