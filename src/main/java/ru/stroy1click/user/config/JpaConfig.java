package ru.stroy1click.user.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {
        "ru.stroy1click.user.entity",
        "ru.stroy1click.outbox.consumer.entity"
})
@EnableJpaRepositories(basePackages = {
        "ru.stroy1click.user.repository",
        "ru.stroy1click.outbox.consumer.repository"
})
public class JpaConfig {

}
