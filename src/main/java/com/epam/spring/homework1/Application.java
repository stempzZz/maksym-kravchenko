package com.epam.spring.homework1;

import com.epam.spring.homework1.config.BeansConfig;
import com.epam.spring.homework1.pet.Cheetah;
import com.epam.spring.homework1.pet.Pet;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(BeansConfig.class);

        Pet pet = context.getBean(Pet.class);
        pet.printPets();

        // Task 10
        System.out.println("----------");
        System.out.println(context.getBean(Cheetah.class));
        System.out.println(context.getBean("secondCheetah", Cheetah.class)); // exception
    }
}
