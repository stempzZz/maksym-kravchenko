package com.epam.spring.time_tracking.lang;

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
