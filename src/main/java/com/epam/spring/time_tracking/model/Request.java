package com.epam.spring.time_tracking.model;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class Request {
    private int id;
    private int activityId;
    private Status status;
    private boolean forDelete;
    private Date createTime;
    private Activity activity;
    private User creator;

    public enum Status {
        WAITING("WAITING"),
        CONFIRMED("CONFIRMED"),
        DECLINED("DECLINED");

        private final String value;

        private static final Map<String, Status> lookup = new HashMap<>();

        static {
            for (Request.Status s : Request.Status.values()) {
                lookup.put(s.getValue(), s);
            }
        }

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Request.Status get(String value) {
            return lookup.get(value);
        }
    }
}
