package com.example.day2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

// Bean names are fully qualified, so that two exercises can each have their own OrderService.
@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class Day2Application {

    public static void main(String[] args) {
        SpringApplication.run(Day2Application.class, args);
    }
}
