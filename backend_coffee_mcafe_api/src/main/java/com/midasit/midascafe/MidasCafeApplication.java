package com.midasit.midascafe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MidasCafeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MidasCafeApplication.class, args);
    }

}
