package com.epam.spring.time_tracking.repository;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Request;

public interface RequestRepo {
    Request createRequestForAdd(Activity activity);

    Request createRequestForRemove(Activity activity);

    Request confirmRequest(int requestId);
}
