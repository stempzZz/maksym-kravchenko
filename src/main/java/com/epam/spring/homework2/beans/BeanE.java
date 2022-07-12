package com.epam.spring.homework2.beans;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class BeanE extends Bean {

    public BeanE() {

    }

    public BeanE(String name, int value) {
        super(name, value);
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("BeanE: postConstruct()");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("BeanE: preDestroy()");
    }

}
