package com.epam.spring.time_tracking.test.util;

import com.epam.spring.time_tracking.model.Activity;
import com.epam.spring.time_tracking.model.Request;
import com.epam.spring.time_tracking.model.enums.status.RequestStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestDataUtilTest {

    public static final Long REQUEST_1_ID = 1L;

    public static final Long REQUEST_2_ID = 2L;

    public static final Long REQUEST_3_ID = 3L;

    public static Request getRequest1(Activity activity, boolean forDelete, RequestStatus status) {
        Request request = new Request();
        request.setId(REQUEST_1_ID);
        request.setStatus(status);
        request.setForDelete(forDelete);
        request.setCreateTime(LocalDateTime.now());
        request.setActivity(activity);
        return request;
    }

    public static Request getRequest2(Activity activity, boolean forDelete, RequestStatus status) {
        Request request = new Request();
        request.setId(REQUEST_2_ID);
        request.setStatus(status);
        request.setForDelete(forDelete);
        request.setCreateTime(LocalDateTime.now());
        request.setActivity(activity);
        return request;
    }

    public static Request getRequest3(Activity activity, boolean forDelete, RequestStatus status) {
        Request request = new Request();
        request.setId(REQUEST_3_ID);
        request.setStatus(status);
        request.setForDelete(forDelete);
        request.setCreateTime(LocalDateTime.now());
        request.setActivity(activity);
        return request;
    }

}
