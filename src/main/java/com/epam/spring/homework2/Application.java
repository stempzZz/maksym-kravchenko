package com.epam.spring.homework2;

import com.epam.spring.homework2.config.ConfigA;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

public class Application {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConfigA.class);

        System.out.println("-------------------------");

        String[] beanNames = context.getBeanDefinitionNames();
        Arrays.stream(beanNames).forEach(System.out::println);

        System.out.println("-------------------------");

        Arrays.stream(beanNames).forEach(name -> System.out.println(context.getBeanDefinition(name)));

        System.out.println("-------------------------");

        context.close();
    }

}
