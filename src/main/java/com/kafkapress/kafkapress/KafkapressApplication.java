package com.kafkapress.kafkapress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com")
public class KafkapressApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkapressApplication.class, args);
    }

}
