package com.epam.spring.homework2.bpp;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        configurableListableBeanFactory.getBeanDefinition("beanB").setInitMethodName("anotherInitBeanB");
        System.out.println("CustomBeanFactoryPostProcessor: postProcessBeanFactory()");
    }

}
