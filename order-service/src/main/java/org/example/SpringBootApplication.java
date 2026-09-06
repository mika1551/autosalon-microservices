package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@org.springframework.boot.autoconfigure.SpringBootApplication(scanBasePackages= {"org.example", "application", "domain", "infrastructure"})
@EnableJpaRepositories(basePackages = "infrastructure.repository")
@EntityScan(basePackages = "domain.model")
@EnableScheduling
public class SpringBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootApplication.class, args);
    }
}
