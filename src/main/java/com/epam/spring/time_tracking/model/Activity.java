package com.epam.spring.time_tracking.model;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Activity {
    private int id;
    private String name;
    private List<Category> categories;
    private String description;
    private String image;
    private int peopleCount;
    private int creatorId;
    private Date createTime;
    private Status status;

    public enum Status {
        BY_ADMIN("BY_ADMIN"),
        BY_USER("BY_USER"),
        ADD_WAITING("ADD_WAITING"),
        ADD_DECLINED("ADD_DECLINED"),
        DEL_WAITING("DEL_WAITING"),
        DEL_CONFIRMED("DEL_CONFIRMED");

        private final String value;
        private static final Map<String, Status> lookup = new HashMap<>();

        static {
            for (Status s : Status.values())
                lookup.put(s.getValue(), s);
        }

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Status get(String value) {
            return lookup.get(value);
        }
    }
}
