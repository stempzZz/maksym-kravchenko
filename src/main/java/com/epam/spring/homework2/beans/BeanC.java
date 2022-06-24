package com.epam.spring.homework2.beans;

import org.springframework.stereotype.Component;

@Component
public class BeanC extends Bean {

    public BeanC() {
        super();
    }

    public BeanC(String name, int value) {
        super(name, value);
    }

    public void initBeanC() {
        System.out.println("BeanC: initBeanC()");
    }

    public void destroyBeanC() {
        System.out.println("BeanC: destroyBeanC()");
    }
}
