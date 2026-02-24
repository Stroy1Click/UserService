package ru.stroy1click.user;

import org.springframework.boot.SpringApplication;
import ru.stroy1click.user.config.TestcontainersConfiguration;

public class TestStroy1ClickUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(Stroy1ClickUserServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
