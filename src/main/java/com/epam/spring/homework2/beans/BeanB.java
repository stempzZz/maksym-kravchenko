package com.epam.spring.homework2.beans;

import org.springframework.stereotype.Component;

@Component
public class BeanB extends Bean {

    public BeanB() {

    }

    public BeanB(String name, int value) {
        super(name, value);
    }

    public void initBeanB() {
        System.out.println("BeanB: initBeanB()");
    }

    public void anotherInitBeanB() {
        System.out.println("BeanB: anotherInitBeanB()");
    }

    public void destroyBeanB() {
        System.out.println("BeanB: destroyBeanB()");
    }
}
