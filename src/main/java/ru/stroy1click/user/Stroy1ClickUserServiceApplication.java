package ru.stroy1click.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class Stroy1ClickUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(Stroy1ClickUserServiceApplication.class, args);
    }

}