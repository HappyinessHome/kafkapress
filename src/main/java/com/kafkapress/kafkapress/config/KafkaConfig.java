package com.kafkapress.kafkapress.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka.sender")
public class KafkaConfig {
    private String topic;
    private int threads=1;
    private String data;
    private int nums=1;
}
