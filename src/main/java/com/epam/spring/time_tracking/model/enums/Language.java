package com.epam.spring.time_tracking.model.enums;

public enum Language {

    EN("en"),
    UA("uk");

    private final String value;

    Language(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
