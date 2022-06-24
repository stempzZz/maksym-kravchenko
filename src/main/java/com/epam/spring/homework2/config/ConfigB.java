package com.epam.spring.homework2.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.epam.spring.homework2.beans", "com.epam.spring.homework2.bpp"})
public class ConfigB {
}
