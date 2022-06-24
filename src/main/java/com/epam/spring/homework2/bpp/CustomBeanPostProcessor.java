package com.epam.spring.homework2.bpp;

import com.epam.spring.homework2.beans.Bean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class CustomBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Bean) {
            if (((Bean) bean).getName() != null && ((Bean) bean).getValue() > 0)
                System.out.println(bean.getClass().getSimpleName() + ": bean is valid");
            else
                System.out.println(bean.getClass().getSimpleName() + ": bean isn't valid");
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}
