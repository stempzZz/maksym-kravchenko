package com.epam.spring.homework2.config;

import com.epam.spring.homework2.beans.BeanB;
import com.epam.spring.homework2.beans.BeanC;
import com.epam.spring.homework2.beans.BeanD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

@Configuration
@Import(ConfigB.class)
@PropertySource("classpath:application.properties")
public class ConfigA {

    @Autowired
    private Environment env;

    @Bean(initMethod = "initBeanB", destroyMethod = "destroyBeanB")
    @DependsOn("beanD")
    public BeanB beanB() {
        return new BeanB(env.getProperty("beanB.name"), Integer.parseInt(env.getProperty("beanB.value")));
    }

    @Bean(initMethod = "initBeanC", destroyMethod = "destroyBeanC")
    @DependsOn("beanB")
    public BeanC beanC() {
        return new BeanC(env.getProperty("beanC.name"), Integer.parseInt(env.getProperty("beanC.value")));
    }

    @Bean(initMethod = "initBeanD", destroyMethod = "destroyBeanD")
    public BeanD beanD() {
        return new BeanD(env.getProperty("beanD.name"), Integer.parseInt(env.getProperty("beanD.value")));
    }

}
