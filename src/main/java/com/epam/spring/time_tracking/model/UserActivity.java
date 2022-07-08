package com.epam.spring.time_tracking.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class UserActivity {

    private Activity activity;
    private User user;
    private LocalDateTime startTime;
    private LocalDateTime stopTime;
    private double spentTime;
    private Status status;

    public enum Status {
        NOT_STARTED("NOT_STARTED"),
        STARTED("STARTED"),
        STOPPED("STOPPED");

        private final String value;

        private static final Map<String, Status> lookup = new HashMap<>();

        static {
            for (UserActivity.Status s : UserActivity.Status.values())
                lookup.put(s.getValue(), s);
        }

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static UserActivity.Status get(String value) {
            return lookup.get(value);
        }
    }

}
