package com.bisa.sis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SisBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SisBackendApplication.class, args);
    }
}
