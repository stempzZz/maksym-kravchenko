package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Request;

import java.util.List;

public interface RequestRepo {

    List<Request> getRequests();

    Request getRequest(int requestId);

    Request createRequestToAdd(Activity activity);

    Request createRequestToRemove(Activity activity);

    Request confirmRequest(int requestId);

    Request declineRequest(int requestId);

    void deleteRequest(int requestId);

    void deleteRequestsWithActivity(int activityId);
}
