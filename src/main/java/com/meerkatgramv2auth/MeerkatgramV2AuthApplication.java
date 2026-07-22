package com.meerkatgramv2auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableJpaAuditing
public class MeerkatgramV2AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeerkatgramV2AuthApplication.class, args);
    }

}
