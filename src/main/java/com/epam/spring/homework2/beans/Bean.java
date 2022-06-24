package com.epam.spring.homework2.beans;

public abstract class Bean {
    private String name;
    private int value;

    public Bean() {

    }

    public Bean(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
