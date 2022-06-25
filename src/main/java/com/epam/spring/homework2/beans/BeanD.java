package com.epam.spring.homework2.beans;

import org.springframework.stereotype.Component;

@Component
public class BeanD extends Bean {

    public BeanD() {

    }

    public BeanD(String name, int value) {
        super(name, value);
    }

    public void initBeanD() {
        System.out.println("BeanD: initBeanD()");
    }

    public void destroyBeanD() {
        System.out.println("BeanD: destroyBeanD()");
    }
}
